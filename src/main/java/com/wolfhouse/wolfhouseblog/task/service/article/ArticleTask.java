package com.wolfhouse.wolfhouseblog.task.service.article;

import com.wolfhouse.wolfhouseblog.common.constant.redis.RedisConstant;
import com.wolfhouse.wolfhouseblog.redis.ArticleRedisService;
import com.wolfhouse.wolfhouseblog.service.mediator.ArticleEsDbMediator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleTask {
    private final ArticleRedisService redisService;
    private final ArticleEsDbMediator mediator;
    private final Environment env;

    {
        log.info("文章定时任务已启动...");
    }

    /**
     * 定时任务方法，用于将文章的浏览量从 Redis 同步到数据库和 Elasticsearch。
     * <p>
     * 该方法每隔30分钟触发一次，通过以下步骤实现：
     * 1. 从 Redis 中获取并删除所有缓存的文章浏览量数据，数据以 Map 的形式返回，其中键为文章 ID，值为对应的浏览量。
     * 2. 将获取到的浏览量数据批量更新到数据库中。
     * 3. 将获取到的浏览量数据同步更新到 Elasticsearch 中。
     * <p>
     * 为了保障文章浏览量正常更新：
     * 1. 获取需要更新浏览量的文章数量
     * 2. 分批更新：
     * - 根据浏览量，从高到底依次更新
     * - 获取并删除一个批次的文章浏览量数据
     * - 更新至数据库
     * - 更新至 Elasticsearch
     * - 重新缓存这个批次的文章内容
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void viewRedisToDb() {
        int batchSize = Integer.parseInt(env.getProperty("custom.task.article.view-batch-size", "100"));
        log.info("文章定时任务启动，从 Redis 同步浏览量至数据库与 ES...");
        Long count = doViewRedisToDb(batchSize);
        if (count == null || count == 0L) {
            log.info("没有要同步的浏览量数据.");
            return;
        }

        log.info("浏览量同步完成");
    }

    private Long doViewRedisToDb(int batchSize) {
        List<Thread> threads = new ArrayList<>();
        // 调用分批方法，创建虚拟线程处理每个分批
        Long viewsCount = redisService.viewsCountBatch(set -> {
            Thread thread = Thread.ofVirtual()
                                  .start(() -> doViewRedisToDbThreadTask(set));
            threads.add(thread);
        }, batchSize);

        // 阻塞
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        return viewsCount;
    }

    private void doViewRedisToDbThreadTask(Set<String> keys) {
        // 1. 提取文章 ID 并获取浏览量
        Map<String, Long> partViews = keys.stream()
                                          .map(k -> {
                                              String[] keyParts = k.split(RedisConstant.SEPARATOR);
                                              String articleId = keyParts[keyParts.length - 1];
                                              Long views = redisService.getViewsAndDelete(Long.parseLong(articleId));
                                              return Map.entry(articleId, views);
                                          })
                                          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 2. 更新至数据库
        try {
            mediator.addViewsToDb(partViews);
        } catch (Exception e) {
            log.error("浏览量同步至数据库失败");
            redisService.increaseViews(partViews);
            return;
        }
        // 更新至 es
        try {
            mediator.addViewsToEs(partViews);
        } catch (Exception e) {
            log.error("浏览量同步至 ES 失败");
            return;
        }
        // 3. 更新缓存
        Integer updates = redisService.updateArticleViews(partViews);
        if (updates != keys.size()) {
            log.warn("Redis 浏览量缓存未全部更新");
        }
    }


    /**
     * 定期备份数据库（wolfBlog）。
     * 可通过以下配置项覆盖默认值：
     * custom.backup.enable: 是否启用（默认 true）
     * custom.backup.cron: 调度表达式（默认 每天03:00）
     * custom.backup.output-dir: 备份文件输出目录（默认 ./backup）
     * custom.backup.mysqldump-cmd: mysqldump 命令名或绝对路径（默认 mysqldump）
     */
    @Scheduled(cron = "${custom.backup.cron:0 0 3 * * ?}")
    public void backupDatabase() {
        boolean enable = Boolean.parseBoolean(env.getProperty("custom.backup.enable", "true"));
        if (!enable) {
            return;
        }
        String database = env.getProperty("custom.backup.database", "wolfBlog");
        String outputDir = env.getProperty("custom.backup.output-dir", "backup");
        String mysqldump = env.getProperty("custom.backup.mysqldump-cmd", "mysqldump");

        String jdbcUrl = env.getProperty("spring.datasource.url", "");
        String username = env.getProperty("spring.datasource.username", "root");
        String password = env.getProperty("spring.datasource.password", "");
        HostPort hp = parseHostPortFromJdbcUrl(jdbcUrl);

        // 创建输出目录
        try {
            Files.createDirectories(Path.of(outputDir));
        } catch (IOException e) {
            log.warn("创建备份目录失败: {}", outputDir, e);
            return;
        }
        String ts = LocalDateTime.now()
                                 .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        File outFile = Path.of(outputDir, database + "-backup-" + ts + ".sql")
                           .toFile();

        List<String> command = new ArrayList<>();
        command.add(mysqldump);
        if (hp.host != null) {
            command.add("-h");
            command.add(hp.host);
        }
        if (hp.port != null) {
            command.add("-P");
            command.add(String.valueOf(hp.port));
        }
        command.add("-u");
        command.add(username);
        // 为避免在某些平台上将 -p 与密码分开导致交互，这里采用 -pPASSWORD 的形式
        command.add("-p" + (password.isBlank() ? "" : password));
        // 仅备份目标数据库
        command.add("--databases");
        command.add(database);
        // 推荐增加更一致的选项
        command.add("--single-transaction");
        command.add("--quick");
        command.add("--skip-lock-tables");

        log.info("开始备份数据库 '{}', 输出: {}", database, outFile.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pb.redirectOutput(outFile);
        try {
            Process p = pb.start();
            boolean finished = p.waitFor(10, TimeUnit.MINUTES);
            if (!finished) {
                p.destroyForcibly();
                log.warn("数据库备份超时，已终止进程");
                safeDelete(outFile);
                return;
            }
            int code = p.exitValue();
            if (code != 0) {
                log.warn("数据库备份失败，退出码: {} (请检查 mysqldump 是否可用以及账号权限)", code);
                safeDelete(outFile);
                return;
            }
        } catch (IOException | InterruptedException e) {
            log.warn("数据库备份过程发生异常", e);
            safeDelete(outFile);
            Thread.currentThread()
                  .interrupt();
            return;
        }
        log.info("数据库备份完成: {}", outFile.getAbsolutePath());
    }

    private void safeDelete(File f) {
        try {
            if (f != null && f.exists()) {
                Files.delete(f.toPath());
            }
        } catch (IOException ignored) {
        }
    }

    private HostPort parseHostPortFromJdbcUrl(String url) {
        // 期望格式: jdbc:mysql://host:port/db?...
        if (url == null || url.isBlank()) {
            return new HostPort("localhost", 3306);
        }
        try {
            String u = url;
            if (u.startsWith("jdbc:")) {
                u = u.substring(5);
            }
            // 将 mysql: 变为 http 兼容的方案名，便于 URI 解析
            if (u.startsWith("mysql:")) {
                u = "//" + u.substring("mysql:".length());
            }
            java.net.URI uri = new java.net.URI("http:" + u);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 3306 : uri.getPort();
            return new HostPort(host, port);
        } catch (Exception e) {
            log.debug("解析 JDBC URL 失败，使用默认 localhost:3306，url={}", url);
            return new HostPort("localhost", 3306);
        }
    }

    private record HostPort(String host, Integer port) {}
}

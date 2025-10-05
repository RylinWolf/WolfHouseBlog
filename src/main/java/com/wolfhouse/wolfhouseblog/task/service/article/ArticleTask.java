package com.wolfhouse.wolfhouseblog.task.service.article;

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
import java.util.*;
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
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void viewRedisToDb() {
        log.info("文章定时任务启动，同步浏览量至数据库与 ES...");
        Map<String, Long> views = redisService.getViewsAndDelete();
        if (views == null) {
            return;
        }
        Set<Long> ids = views.keySet()
                             .stream()
                             .map(Long::valueOf)
                             .collect(Collectors.toSet());
        try {
            mediator.addViewsToDb(views);
        } catch (Exception e) {
            log.warn("数据库浏览量同步错误");
            return;
        }
        Set<Long> esRes = mediator.addViewsToEs(views);
        if (ids.size() != esRes.size()) {
            HashSet<Long> failed = new HashSet<>(esRes);
            log.warn("ES 浏览量同步异常, 失败 ID: {}", failed);
        }
        log.info("浏览量同步完成");
        // 移除浏览量 ID
        redisService.decreaseViews(views);
        // 更新 redis 的浏览量
        if (!redisService.addViews(views)) {
            redisService.removeArticleCache(views.keySet());
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

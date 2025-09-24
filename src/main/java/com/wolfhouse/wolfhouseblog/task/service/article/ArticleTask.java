package com.wolfhouse.wolfhouseblog.task.service.article;

import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.redis.ArticleRedisService;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleTask {
    private final ArticleRedisService redisService;
    private final ArticleElasticServiceImpl esService;
    @Resource(name = "articleServiceImpl")
    private ArticleService articleService;

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
    @Scheduled(cron = "0 30 * * * ?")
    public void viewRedisToDb() {
        log.info("文章定时任务启动，同步浏览量至数据库与 ES...");
        Map<String, Long> views = redisService.getViews();
        if (views == null) {
            return;
        }
        Set<Long> ids = views.keySet()
                             .stream()
                             .map(Long::valueOf)
                             .collect(Collectors.toSet());
        try {
            articleService.addViews(views);
        } catch (Exception e) {
            log.warn("数据库浏览量同步错误");
            return;
        }
        Set<Long> esRes = esService.addViews(views);
        if (ids.size() != esRes.size()) {
            HashSet<Long> failed = new HashSet<>(esRes);
            log.warn("ES 浏览量同步异常, 失败 ID: {}", failed);
        }
        log.info("浏览量同步完成");
        // 移除 ID
        redisService.deleteViews(ids);
    }
}

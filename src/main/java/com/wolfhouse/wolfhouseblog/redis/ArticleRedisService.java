package com.wolfhouse.wolfhouseblog.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhouse.wolfhouseblog.common.constant.redis.ArticleRedisConstant;
import com.wolfhouse.wolfhouseblog.common.constant.redis.RedisConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文章 Redis 服务
 *
 * @author linexsong
 */
@Slf4j
@Component
public class ArticleRedisService {
    private ValueOperations<String, PageResult<ArticleBriefVo>> pageOps;
    private final RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "jsonNullableObjectMapper")
    private ObjectMapper objectMapper;
    /** 基础缓存过期时间，三天 */
    private static final Long BASE_TIME_OUT = 3 * 24 * 60L;

    @PostConstruct
    public void init() {
        RedisTemplate<String, PageResult<ArticleBriefVo>> pageTemplate = new RedisTemplate<>();
        pageTemplate.setConnectionFactory(redisTemplate.getConnectionFactory());
        pageTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        pageTemplate.setValueSerializer(new RedisSerializer<PageResult<ArticleBriefVo>>() {

            @Override
            public byte[] serialize(PageResult<ArticleBriefVo> value) throws SerializationException {
                try {
                    return objectMapper.writeValueAsBytes(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public PageResult<ArticleBriefVo> deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null) {
                    return null;
                }
                try {
                    LinkedHashMap<?, ?> map = objectMapper.readValue(bytes, LinkedHashMap.class);
                    return objectMapper.convertValue(map, PageResult.class);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        pageTemplate.afterPropertiesSet();

        pageOps = pageTemplate.opsForValue();
    }

    @Autowired
    public ArticleRedisService(RedisTemplate<String, Object> redisTemplate,
                               ObjectMapper defaultObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = defaultObjectMapper;
    }

    /**
     * 将文章数据缓存到 Redis 中，如果文章已存在则更新缓存。
     * 使用分布式锁确保并发安全。
     *
     * @param articleVo 需要缓存的文章数据，包含文章 ID、标题、内容等信息
     * @return 操作是否成功，获取锁失败时返回 null
     */
    @Nullable
    public Boolean cacheOrUpdateArticle(ArticleVo articleVo) {
        String lock = ArticleRedisConstant.VO_LOCK.formatted(articleVo.getId());
        String key = ArticleRedisConstant.VO.formatted(articleVo.getId());
        // 上锁
        for (int i = 0; i < RedisConstant.LOCK_MAX_RETRY; i++) {
            // 获取锁
            if (!getLock(lock)) {
                try {
                    Thread.sleep(100);
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread()
                          .interrupt();
                    log.warn("线程在等待锁时被中断");
                    return null;
                }
            }
            try {
                ValueOperations<String, Object> ops = redisTemplate.opsForValue();
                if (redisTemplate.hasKey(key)) {
                    redisTemplate.delete(key);
                }
                ops.set(ArticleRedisConstant.VO.formatted(articleVo.getId()),
                        articleVo,
                        randTimeout(),
                        TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new ServiceException(e.getMessage(), e);
            } finally {
                // 解锁
                redisTemplate.opsForValue()
                             .getAndDelete(lock);
            }
            return true;
        }
        // 未成功获得锁
        return null;
    }

    /**
     * 删除指定文章的缓存数据。
     *
     * @param articleId 要删除缓存的文章 ID
     */
    @Nullable
    public Boolean removeArticleCache(Long articleId) {
        String key = ArticleRedisConstant.VO.formatted(articleId);
        String lock = ArticleRedisConstant.VO_LOCK.formatted(articleId);
        // 尝试获取锁
        if (!getLock(lock)) {
            return null;
        }
        Boolean delete = redisTemplate.delete(key);
        // 释放锁
        redisTemplate.opsForValue()
                     .getAndDelete(lock);
        return delete;
    }

    /**
     * 批量删除指定文章的缓存数据。
     *
     * @param articleIds 需要删除缓存的文章 ID 集合
     */
    public void removeArticleCache(Collection<?> articleIds) {
        articleIds.forEach(id -> removeArticleCache(Long.parseLong(id.toString())));
    }

    public ArticleVo getCachedArticle(Long id) {
        return objectMapper.convertValue(redisTemplate.opsForValue()
                                                      .getAndExpire(ArticleRedisConstant.VO.formatted(id),
                                                                    randTimeout(),
                                                                    TimeUnit.MINUTES),
                                         ArticleVo.class);
    }

    /**
     * 增加指定文章的浏览量。
     *
     * @param articleId 文章的唯一标识 ID
     */
    public void increaseView(Long articleId) {
        String key = ArticleRedisConstant.VIEWS.formatted(articleId);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        // 文章 key 不存在
        if (!redisTemplate.hasKey(key)) {
            boolean lockAcquired = false;
            try {
                // 获得锁
                lockAcquired = Boolean.TRUE.equals(ops.setIfAbsent(ArticleRedisConstant.VIEWS_LOCK,
                                                                   0,
                                                                   ArticleRedisConstant.LOCK_TIME_SECONDS,
                                                                   TimeUnit.SECONDS));
                if (lockAcquired) {
                    // 双重检查锁定
                    if (!redisTemplate.hasKey(key)) {
                        ops.set(key, 0, ArticleRedisConstant.VIEWS_EXPIRE_MINUTES, TimeUnit.MINUTES);

                    }
                } else {
                    // 未获取到锁，等待一会儿确保可以正常获取到 key
                    Thread.sleep(100);
                }
            } catch (Exception ignored) {} finally {
                // 移除锁
                if (lockAcquired) {
                    ops.getAndDelete(ArticleRedisConstant.VIEWS_LOCK);
                }
            }
        }
        // 自增
        ops.increment(key);
        // 刷新过期时间
        redisTemplate.expire(key, ArticleRedisConstant.VIEWS_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }


    /**
     * 获取并删除所有文章的浏览量缓存。
     *
     * @return 包含文章ID和对应浏览量的Map，如果没有缓存数据则返回null
     */
    public Map<String, Long> getViewsAndDelete() {
        // 获取所有缓存的浏览量键
        Set<String> keys = redisTemplate.keys(ArticleRedisConstant.VIEWS.formatted("*"));
        if (keys.isEmpty()) {
            return null;
        }
        // 构建缓存至 Map，结构为 键（文章 ID）：浏览量
        HashMap<String, Long> views = new HashMap<>(keys.size());
        keys.forEach(k -> {
            // 从键中获取文章 ID
            String[] split = k.split(RedisConstant.SEPARATOR);
            String articleId = split[split.length - 1];
            Object o = redisTemplate.opsForValue()
                                    .getAndDelete(k);
            if (o == null) {
                return;
            }
            views.put(articleId, Long.parseLong(o.toString()));
        });
        return views;
    }

    /**
     * 获取并删除指定文章的浏览量缓存。
     *
     * @param articleId 文章ID
     * @return 文章的浏览量，如果缓存不存在则返回null
     */
    public Long getViewsAndDelete(Long articleId) {
        String key = ArticleRedisConstant.VIEWS.formatted(articleId);
        Object o = redisTemplate.opsForValue()
                                .getAndDelete(key);
        if (o == null) {
            return null;
        }
        return Long.parseLong(o.toString());
    }

    /**
     * 获取指定文章的浏览量缓存。
     *
     * @param articleId 文章ID
     * @return 文章的浏览量，如果缓存不存在则返回null
     */
    public Long getViews(Long articleId) {
        Object o = redisTemplate.opsForValue()
                                .get(ArticleRedisConstant.VIEWS.formatted(articleId));
        if (o == null) {
            return null;
        }
        return Long.parseLong(o.toString());
    }


    /**
     * 减少指定文章的浏览量。
     *
     * @param views 包含文章 ID 及其对应需要减少的浏览量的映射。键为文章 ID 的字符串表示，值为减少的浏览量。
     */
    public void decreaseViews(Map<String, Long> views) {
        views.forEach((k, v) -> {
            Object o = redisTemplate.opsForValue()
                                    .get(ArticleRedisConstant.VIEWS.formatted(k));
            if (o == null) {
                return;
            }
            redisTemplate.opsForValue()
                         .decrement(ArticleRedisConstant.VIEWS.formatted(k), v);
        });
    }

    /**
     * 减少指定文章的浏览量。
     *
     * @param articleId 文章的唯一标识 ID
     * @param views     需要减少的浏览量
     */
    public void decreaseViews(Long articleId, Long views) {
        redisTemplate.opsForValue()
                     .decrement(ArticleRedisConstant.VIEWS.formatted(articleId), views);
    }


    /**
     * 缓存文章简要信息的分页查询结果。
     *
     * @param dto    查询条件数据传输对象
     * @param briefs 文章简要信息的分页结果
     */
    public void cacheBriefs(ArticleQueryPageDto dto, PageResult<ArticleBriefVo> briefs) {
        pageOps.set(ArticleRedisConstant.QUERY_BRIEF.formatted(dto), briefs, randTimeout(), TimeUnit.MINUTES);
    }

    /**
     * 获取已缓存的文章简要信息分页查询结果。
     *
     * @param dto 查询条件数据传输对象
     * @return 文章简要信息的分页结果
     */
    public PageResult<ArticleBriefVo> getCachedBrief(ArticleQueryPageDto dto) {
        return pageOps.getAndExpire(ArticleRedisConstant.QUERY_BRIEF.formatted(dto),
                                    randTimeout(),
                                    TimeUnit.MINUTES);
    }

    /**
     * 生成随机的缓存过期时间。
     * 在基础过期时间上增加0-60分钟的随机值，避免缓存同时失效。
     *
     * @return 随机生成的过期时间（分钟）
     */
    private Long randTimeout() {
        return BASE_TIME_OUT + new Random().nextLong(60);
    }


    /**
     * 批量增加文章的浏览量。
     * 使用分布式锁确保并发安全。
     *
     * @param views 文章ID和需要增加的浏览量的映射
     * @return 操作是否成功
     */
    public Boolean addViews(Map<String, Long> views) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        // 获取块级锁
        // TODO 锁面临其他异步问题
        String lock = ArticleRedisConstant.VIEWS_LOCK.formatted(RedisConstant.BLOCK_LOCK);
        if (!getLock(lock)) {
            return false;
        }
        try {
            // 获取每一个需要更新浏览量的文章
            views.forEach((k, v) -> {
                var key = ArticleRedisConstant.VO.formatted(k);
                Object o = ops.get(key);
                if (o == null) {
                    return;
                }
                ArticleVo articleVo = objectMapper.convertValue(o, ArticleVo.class);
                // 更新浏览量
                articleVo.setViews(articleVo.getViews() + v);
                ops.set(key, articleVo, randTimeout(), TimeUnit.MINUTES);
            });
        } catch (Exception ignored) {
            return false;
        } finally {
            // 释放锁
            ops.getAndDelete(lock);
        }
        return true;
    }

    /**
     * 为文章增加一个点赞。
     * 如果点赞记录不存在，将尝试通过加锁的方式初始化点赞记录。
     *
     * @param articleId 文章的唯一标识 ID
     * @return 执行结果，如果成功增加点赞则返回 true；否则返回 false
     * @throws InterruptedException 在尝试获取锁或初始化点赞数据时，线程被中断
     */
    public Boolean like(Long articleId) throws InterruptedException {
        String key = ArticleRedisConstant.LIKE.formatted(articleId);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        int maxRetries = 3;
        int retries = 0;
        while (!redisTemplate.hasKey(key)) {
            String lock = ArticleRedisConstant.LIKE_LOCK.formatted(RedisConstant.BLOCK_LOCK);
            if (Boolean.FALSE.equals(ops.setIfAbsent(lock, 0))) {
                // 未抢到锁
                if (retries >= maxRetries) {
                    // 超过最大重试次数
                    return false;
                }
                retries++;
                Thread.sleep(1000);
                continue;
            }
            // 抢到锁，初始化点赞
            ops.set(key, 0, randTimeout(), TimeUnit.MINUTES);
            break;
        }
        // 自增点赞量
        ops.increment(key);
        return true;
    }

    /**
     * 获取指定文章的点赞数。
     *
     * @param articleId 文章的唯一标识 ID
     * @return 文章的点赞数，如果未找到对应的缓存数据，返回 0
     */
    public Long getLikes(Long articleId) {
        String key = ArticleRedisConstant.LIKE.formatted(articleId);
        if (!redisTemplate.hasKey(key)) {
            return 0L;
        }

        return (Long) redisTemplate.opsForValue()
                                   .get(key);
    }

    /**
     * 删除指定文章的点赞记录缓存。
     *
     * @param articleId 文章的唯一标识 ID
     * @return 如果缓存删除成功返回 true；否则返回 false
     */
    public Boolean deleteLike(Long articleId) {
        String key = ArticleRedisConstant.LIKE.formatted(articleId);
        return redisTemplate.hasKey(key) && redisTemplate.delete(key);
    }

    /**
     * 尝试为指定的文章 ID 设置指定锁
     *
     * @param lock 要设置的锁 ID
     * @return 如果成功设置锁返回 true，否则返回 false
     */
    private Boolean getLock(String lock) {
        return Boolean.TRUE.equals(
            redisTemplate.opsForValue()
                         .setIfAbsent(lock, 0, ArticleRedisConstant.LOCK_TIME_SECONDS, TimeUnit.SECONDS));
    }
}

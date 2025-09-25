package com.wolfhouse.wolfhouseblog.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhouse.wolfhouseblog.common.constant.redis.ArticleRedisConstant;
import com.wolfhouse.wolfhouseblog.common.constant.redis.RedisConstant;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

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
     * 将文章数据缓存到 Redis 中。
     *
     * @param articleVo 需要缓存的文章数据，包含文章 ID、标题、内容等信息
     */
    public void cacheArticle(ArticleVo articleVo) {
        redisTemplate.opsForValue()
                     .set(ArticleRedisConstant.VO.formatted(articleVo.getId()),
                          articleVo,
                          randTimeout(),
                          TimeUnit.MINUTES);
    }

    /**
     * 删除指定文章的缓存数据。
     *
     * @param articleId 要删除缓存的文章 ID
     */
    public void removeArticleCache(Long articleId) {
        redisTemplate.delete(ArticleRedisConstant.VO.formatted(articleId));
    }

    /**
     * 增加指定文章的浏览量。
     *
     * @param articleId 文章的唯一标识 ID
     */
    public void increaseView(Long articleId) {
        String key = ArticleRedisConstant.VIEW.formatted(articleId);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        // 文章 key 不存在
        if (!redisTemplate.hasKey(key)) {
            try {
                // 获得锁
                if (Boolean
                    .TRUE
                    .equals(ops.setIfAbsent(ArticleRedisConstant.LOCK,
                                            0,
                                            ArticleRedisConstant.LOCK_TIME_SECONDS,
                                            TimeUnit.SECONDS))) {
                    // 双重检查锁定
                    if (!redisTemplate.hasKey(key)) {
                        ops.set(key, 0, ArticleRedisConstant.VIEWS_EXPIRE_MINUTES, TimeUnit.MINUTES);

                    }
                } else {
                    // 未获取到锁，等待一会儿确保可以正常获取到 key
                    Thread.sleep(100);
                }
            } catch (Exception ignored) {
                // 移除锁
                ops.getAndDelete(ArticleRedisConstant.LOCK);
            }
        }
        // 自增
        ops.increment(key);
        // 刷新过期时间
        ops.set(key, Objects.requireNonNull(ops.get(key)), ArticleRedisConstant.VIEWS_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }


    public Map<String, Long> getViews() {
        // 获取所有缓存的浏览量键
        Set<String> keys = redisTemplate.keys(ArticleRedisConstant.VIEW.formatted("*"));
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

    public Long getViews(Long articleId) {
        String key = ArticleRedisConstant.VIEW.formatted(articleId);
        Object o = redisTemplate.opsForValue()
                                .getAndDelete(key);
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
                                    .get(ArticleRedisConstant.VIEW.formatted(k));
            if (o == null) {
                return;
            }
            redisTemplate.opsForValue()
                         .decrement(ArticleRedisConstant.VIEW.formatted(k), v);
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
                     .decrement(ArticleRedisConstant.VIEW.formatted(articleId), views);
    }


    public void cacheBriefs(ArticleQueryPageDto dto, PageResult<ArticleBriefVo> briefs) {
        pageOps.set(ArticleRedisConstant.QUERY_BRIEF.formatted(dto), briefs, randTimeout(), TimeUnit.MINUTES);
    }

    public PageResult<ArticleBriefVo> getCachedBrief(ArticleQueryPageDto dto) {
        return pageOps.getAndExpire(ArticleRedisConstant.QUERY_BRIEF.formatted(dto),
                                    randTimeout(),
                                    TimeUnit.MINUTES);
    }

    private Long randTimeout() {
        return BASE_TIME_OUT + new Random().nextLong(60);
    }

}

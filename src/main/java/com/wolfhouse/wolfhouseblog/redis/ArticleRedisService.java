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
    private final ObjectMapper objectMapper;
    private static final Long BASE_TIME_OUT = 24 * 60L;

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
     * 增加指定文章的浏览量。
     *
     * @param articleId 文章的唯一标识 ID
     */
    public void increaseView(Long articleId) {
        String key = ArticleRedisConstant.VIEW.formatted(articleId);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        if (!redisTemplate.hasKey(key)) {
            ops.set(key, 0);
        }
        redisTemplate.opsForValue()
                     .increment(key);
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

    public void deleteViews(Set<Long> articleIds) {
        articleIds.forEach(id -> redisTemplate.delete(ArticleRedisConstant.VIEW.formatted(id)));
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

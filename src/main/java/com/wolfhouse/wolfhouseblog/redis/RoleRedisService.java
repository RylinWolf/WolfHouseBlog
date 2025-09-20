package com.wolfhouse.wolfhouseblog.redis;

import com.wolfhouse.wolfhouseblog.common.constant.redis.UserRedisConstant;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author linexsong
 */
@Component
public class RoleRedisService {
    /** 基础过期时间 */
    public static final Long BASE_TIME_OUT = 24 * 60L;

    private final ValueOperations<String, Object> valueOps;

    @Autowired
    public RoleRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.valueOps = redisTemplate.opsForValue();
    }

    @SuppressWarnings("unchecked")
    public List<Authority> getAuthorities(final Long userId) {
        Object o = valueOps.get(UserRedisConstant.AUTHORITIES.formatted(userId));
        return o == null ? null : (List<Authority>) o;
    }

    public void saveAuthorities(final Long userId, final List<Authority> authorities) {
        String key = UserRedisConstant.AUTHORITIES.formatted(userId);
        valueOps.set(key, authorities, randTimeout(), TimeUnit.MINUTES);
    }

    /**
     * 根据 token 获取缓存的 userId
     *
     * @param token token
     * @return token 对应的 userId
     */
    public Long getAndRefreshToken(final String token) {
        Object o = valueOps.getAndExpire(UserRedisConstant.TOKEN.formatted(token), randTimeout(), TimeUnit.MINUTES);
        return o != null ? Long.parseLong(o.toString()) : null;
    }

    public void saveTokenCache(final String token, final String userId) {
        valueOps.set(UserRedisConstant.TOKEN.formatted(token), userId, randTimeout(), TimeUnit.MINUTES);
    }

    /**
     * 获取随机时间延迟
     * 范围波动：30
     *
     * @return Long 随机的时间延迟
     */
    private Long randTimeout() {
        return BASE_TIME_OUT + new Random().nextLong(30);
    }
}

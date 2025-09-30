package com.wolfhouse.wolfhouseblog.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhouse.wolfhouseblog.common.constant.redis.UserRedisConstant;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Random;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class UserRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "jsonNullableObjectMapper")
    private ObjectMapper objectMapper;

    /**
     * 缓存用户信息（带过期时间）
     */
    public void userInfoCache(UserVo userVo) {
        // 默认2小时过期
        userInfoCache(userVo, getExpireTime());
    }

    /**
     * 缓存用户信息并设置过期时间
     */
    public void userInfoCache(UserVo userVo, Duration expireTime) {
        String key = getKey(userVo.getId());
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();

        // 存储用户信息
        ops.putAll(key, objectMapper.convertValue(userVo, Map.class));

        // 设置过期时间
        redisTemplate.expire(key, expireTime);
    }

    /**
     * 获取用户信息
     */
    public UserVo getUserInfo(Long userId) {
        String key = getKey(userId);
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();

        Map<String, Object> userMap = ops.entries(key);
        if (userMap.isEmpty()) {
            return null;
        }

        // 更新过期时间
        redisTemplate.expire(key, getExpireTime());

        return objectMapper.convertValue(userMap, UserVo.class);
    }

    /**
     * 更新用户特定字段
     */
    public void updateUserField(Long userId, String field, Object value) {
        String key = getKey(userId);
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();

        ops.put(key, field, value);

        // 刷新过期时间（可选）
        redisTemplate.expire(key, getExpireTime());
    }

    /**
     * 删除用户缓存
     */
    public void deleteUserCache(Long userId) {
        String key = getKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * 检查用户缓存是否存在
     */
    public boolean existsUserCache(Long userId) {
        String key = getKey(userId);
        return redisTemplate.hasKey(key);
    }


    private String getKey(Long userId) {
        return UserRedisConstant.INFO.formatted(userId);
    }

    private Duration getExpireTime() {
        return Duration.ofMinutes(UserRedisConstant.INFO_TIMEOUT_MINUTES)
                       .plus(Duration.ofMinutes(new Random().nextInt(10, 30)));
    }


}

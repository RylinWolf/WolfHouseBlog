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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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


    /**
     * 根据提供的用户 ID 集合，从缓存中获取对应的用户信息列表。
     * 对于未在缓存中存在的用户 ID，不会包含在返回结果中。
     *
     * @param ids 用户 ID 集合，用于指定需要从缓存中获取信息的用户。
     * @return 包含缓存中存在的用户信息的列表，如果所有 ID 均未命中缓存，则返回空列表。
     */
    public Map<Long, UserVo> getCachedUsers(Set<Long> ids) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        Map<Long, UserVo> users = new HashMap<>(ids.size());

        for (Long id : ids) {
            if (!redisTemplate.hasKey(getKey(id))) {
                continue;
            }
            users.put(id, objectMapper.convertValue(ops.entries(getKey(id)), UserVo.class));
        }
        return users;
    }

    public void avatarFingerprintCache(String fingerprint, String filepath) {
        // 指纹有效期 15 分钟
        redisTemplate.opsForValue()
                     .set(UserRedisConstant.AVATAR_FINGERPRINT.formatted(fingerprint),
                          filepath,
                          Duration.ofMinutes(15));
    }

    public String getAvatarByFingerPrint(String avatar) {
        if (avatar == null || avatar.isBlank()) {
            return null;
        }
        return String.valueOf(redisTemplate.opsForValue()
                                           .getAndDelete(UserRedisConstant.AVATAR_FINGERPRINT.formatted(avatar)));
    }
}

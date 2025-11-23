package com.wolfhouse.wolfhouseblog.common.utils.redisutil;

import lombok.NonNull;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * 操作 Redis 的工具类
 *
 * @author Rylin Wolf
 */
public class RedisUtil {
    public final RedisTemplate<String, Object> redisTemplate;
    public final ValueOperations<String, Object> opsForValue;
    public final SetOperations<String, Object> opsForSet;
    public final ZSetOperations<String, Object> opsForZSet;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.opsForValue = redisTemplate.opsForValue();
        this.opsForSet = redisTemplate.opsForSet();
        this.opsForZSet = redisTemplate.opsForZSet();
    }

    public Long getAndIncrease(@NonNull String key, int value) {
        return opsForValue.increment(key, value);
    }

    public Long getAndDecrease(@NonNull String key, int value) {
        return opsForValue.decrement(key, value);
    }

    // region set 方法

    public Long addSetValue(@NonNull String key, Object... value) {
        return opsForSet.add(key, value);
    }

    public void removeSetValue(@NonNull String key, Object... value) {
        opsForSet.remove(key, value);
    }

    public Object popSetValue(@NonNull String key, Object value) {
        return opsForSet.pop(key);
    }

    public Set<Object> getSetMembers(@NonNull String key) {
        return opsForSet.members(key);
    }

    public Long sizeOfSetValue(@NonNull String key) {
        return opsForSet.size(key);
    }

    public Boolean isSetValueMember(@NonNull String key, Object value) {
        return opsForSet.isMember(key, value);
    }

    public Boolean addZSetValue(@NonNull String key, Object value, double score) {
        return opsForZSet.add(key, value, score);
    }

    public Long removeZSetValue(@NonNull String key) {
        return opsForZSet.remove(key);
    }

    public Double incrementZSetValue(@NonNull String key, Object value, double score) {
        return opsForZSet.incrementScore(key, value, score);
    }


    // endregion

    // region value 方法

    public void setValue(@NonNull String key, Object value) {
        this.opsForValue.set(key, value);
    }

    public void setValueExpire(@NonNull String key, Object value, Duration duration) {
        this.opsForValue.set(key, value, duration);
    }


    public Boolean setValueIfAbsent(@NonNull String key, Object value) {
        return this.opsForValue.setIfAbsent(key, value);
    }

    public Object getValue(@NonNull String key) {
        return this.opsForValue.get(key);
    }

    public Object getValueAndExpire(@NonNull String key, Duration duration) {
        Object value = this.opsForValue.get(key);
        this.redisTemplate.expire(key, duration);
        return value;
    }

    public Object getValueAndDelete(@NonNull String key) {
        return opsForValue.getAndDelete(key);
    }

    // endregion

    // region 内置方法

    public Boolean hasKey(@NonNull String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean delete(@NonNull String key) {
        return redisTemplate.delete(key);
    }

    public Boolean expire(@NonNull String key, Duration duration) {
        return redisTemplate.expire(key, duration);
    }

    // endregion

    // region 键匹配

    /**
     * 扫描 Redis 数据库中与指定匹配模式相符的键。
     *
     * @param pattern 匹配模式，支持通配符，例如 "user:*"。
     * @param count   每次扫描的最大数量，控制扫描结果的批次大小。
     * @return 返回匹配的键值集合。
     */
    public Set<String> keysMatch(@NonNull String pattern, int count) {
        HashSet<String> keys = new HashSet<>();
        try (Cursor<String> cursor = redisTemplate.scan(
            ScanOptions.scanOptions()
                       .match(pattern)
                       .count(count)
                       .build())) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
        }
        return keys;
    }

    // endregion
}

package com.wolfhouse.wolfhouseblog.redis;

import com.wolfhouse.wolfhouseblog.common.utils.redisutil.RedisKeyUtil;
import com.wolfhouse.wolfhouseblog.common.utils.redisutil.RedisUtil;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Set;

/**
 * 业务服务相关的 Redis 工具类，整合了 Redis 键工具，自动通过工具获取 Redis 对应的键并进行操作
 *
 * @author Rylin Wolf
 */
public class ServiceRedisUtil extends RedisUtil {
    public final RedisKeyUtil redisKeyUtil;

    public ServiceRedisUtil(RedisTemplate<String, Object> redisTemplate, RedisKeyUtil redisKeyUtil) {
        super(redisTemplate);
        this.redisKeyUtil = redisKeyUtil;
    }

    @Override
    public Set<String> keysMatch(@NonNull String formatKey, int count) {
        formatKey = redisKeyUtil.getKey(formatKey);
        formatKey = formatKey.formatted("*");
        return super.keysMatch(formatKey, count);
    }

    @Override
    public Boolean expire(@NonNull String key, Duration duration) {
        key = redisKeyUtil.getKey(key);
        return super.expire(key, duration);
    }

    public Boolean expire(@NonNull String key, Duration duration, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.expire(key, duration);
    }

    @Override
    public Boolean delete(@NonNull String key) {
        key = redisKeyUtil.getKey(key);
        return super.delete(key);
    }

    public Boolean delete(@NonNull String key, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.delete(key);
    }

    @Override
    public Boolean hasKey(@NonNull String key) {
        key = redisKeyUtil.getKey(key);
        return super.hasKey(key);
    }

    public Boolean hasKey(@NonNull String key, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.hasKey(key);
    }

    @Override
    public Object getValueAndDelete(@NonNull String key) {
        key = redisKeyUtil.getKey(key);
        return super.getValueAndDelete(key);
    }

    public Object getValueAndDelete(@NonNull String key, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted((Object[]) formats);
        return super.getValueAndDelete(key);
    }

    @Override
    public Object getValueAndExpire(@NonNull String key, Duration duration) {
        key = redisKeyUtil.getKey(key);
        return super.getValueAndExpire(key, duration);
    }

    public Object getValueAndExpire(@NonNull String key, Duration duration, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.getValueAndExpire(key, duration);
    }

    @Override
    public Object getValue(@NonNull String key) {
        key = redisKeyUtil.getKey(key);
        return super.getValue(key);
    }

    public Object getValue(@NonNull String key, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.getValue(key);
    }

    @Override
    public Boolean setValueIfAbsent(@NonNull String key, Object value) {
        key = redisKeyUtil.getKey(key);
        return super.setValueIfAbsent(key, value);
    }

    public Boolean setValueIfAbsent(@NonNull String key, Object value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.setValueIfAbsent(key, value);
    }

    @Override
    public void setValueExpire(@NonNull String key, Object value, Duration duration) {
        key = redisKeyUtil.getKey(key);
        super.setValueExpire(key, value, duration);
    }

    public void setValueExpire(@NonNull String key, Object value, Duration duration, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        super.setValueExpire(key, value, duration);
    }

    @Override
    public void setValue(@NonNull String key, Object value) {
        key = redisKeyUtil.getKey(key);
        super.setValue(key, value);
    }

    public void setValue(@NonNull String key, Object value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        super.setValue(key, value);
    }

    @Override
    public Long getAndDecrease(@NonNull String key, int value) {
        key = redisKeyUtil.getKey(key);
        return super.getAndDecrease(key, value);
    }

    public Long getAndDecrease(@NonNull String key, int value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.getAndDecrease(key, value);
    }

    @Override
    public Long getAndIncrease(@NonNull String key, int value) {
        key = redisKeyUtil.getKey(key);
        return super.getAndIncrease(key, value);
    }

    public Long getAndIncrease(@NonNull String key, int value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.getAndIncrease(key, value);
    }

    // region ZSet

    @Override
    public Double incrementZSetValue(@NonNull String key, Object value, double score) {
        key = redisKeyUtil.getKey(key);
        return super.incrementZSetValue(key, value, score);
    }

    public Double incrementZSetValue(@NonNull String key, Object value, double score, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.incrementZSetValue(key, value, score);
    }


    @Override
    public Long removeZSetValue(@NonNull String key) {
        key = redisKeyUtil.getKey(key);
        return super.removeZSetValue(key);
    }

    public Long removeZSetValue(@NonNull String key, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.removeZSetValue(key);
    }

    @Override
    public Boolean addZSetValue(@NonNull String key, Object value, double score) {
        key = redisKeyUtil.getKey(key);
        return super.addZSetValue(key, value, score);
    }

    public Boolean addZSetValue(@NonNull String key, Object value, double score, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.addZSetValue(key, value, score);
    }

    // endregion

    // region set

    @Override
    public Boolean isSetValueMember(@NonNull String key, Object value) {
        key = redisKeyUtil.getKey(key);
        return super.isSetValueMember(key, value);
    }

    public Boolean isSetValueMember(@NonNull String key, Object value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.isSetValueMember(key, value);
    }

    @Override
    public Long sizeOfSetValue(@NonNull String key) {
        key = redisKeyUtil.getKey(key);
        return super.sizeOfSetValue(key);
    }

    public Long sizeOfSetValue(@NonNull String key, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.sizeOfSetValue(key);
    }

    @Override
    public Object popSetValue(@NonNull String key, Object value) {
        key = redisKeyUtil.getKey(key);
        return super.popSetValue(key, value);
    }

    public Object popSetValue(@NonNull String key, Object value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.popSetValue(key, value);
    }

    @Override
    public void removeSetValue(@NonNull String key, Object... value) {
        key = redisKeyUtil.getKey(key);
        super.removeSetValue(key, value);
    }

    public void removeSetValue(@NonNull String key, Object[] value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        super.removeSetValue(key, value);
    }


    @Override
    public Long addSetValue(@NonNull String key, Object... value) {
        key = redisKeyUtil.getKey(key);
        return super.addSetValue(key, value);
    }

    public void addSetValue(@NonNull String key, Object value, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        super.addSetValue(key, value);
    }

    /**
     * 向指定的键添加多个集合值。
     * 该方法会调用父类的 {@code addSetValue} 方法实现实际的操作。
     * <p>
     * <b>注意，该方法的 key 应为 Redis 中的实际键名</b>
     *
     * @param key   Redis 的键名，不能为空。
     * @param value 可变参数，表示需要添加到集合中的值。
     */
    public void addSetValueWithCustomKey(@NonNull String key, Object... value) {
        super.addSetValue(key, value);
    }

    @Override
    public Set<Object> getSetMembers(@NonNull String key) {
        key = redisKeyUtil.getKey(key);
        return super.getSetMembers(key);
    }

    public Set<Object> getSetMembers(@NonNull String key, Object... formats) {
        key = redisKeyUtil.getKey(key)
                          .formatted(formats);
        return super.getSetMembers(key);
    }

    // endregion


}

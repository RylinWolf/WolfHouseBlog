package com.wolfhouse.wolfhouseblog.common.utils.redisutil;

import lombok.Data;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Redis键值工具类，用于管理和生成Redis的键。
 * 该工具类支持以下功能：
 * - 通过前缀和分隔符自定义键的格式
 * - 支持二级前缀的设置
 * - 提供键值的注册、获取、删除等管理功能
 *
 * @author Rylin Wolf
 */
@Data
public class RedisKeyUtil {
    /** 默认的键值分隔符 */
    public static final String DEFAULT_KEY_SEPARATOR = ":";
    /** 默认的键值前缀 */
    public static final String DEFAULT_KEY_PREFIX = "service";

    /** 存储已注册的键值映射关系 */
    private final ConcurrentHashMap<String, String> keyMap = new ConcurrentHashMap<>();
    /** 分隔符 不应多次修改 */
    private String separator;
    /** 主要前缀 不应多次修改 */
    private String prefix;
    /** 二级前缀 */
    private String secondaryPrefix = "";

    public RedisKeyUtil(String prefix, String separator) {
        this.prefix = prefix == null ? DEFAULT_KEY_PREFIX : prefix;
        this.separator = separator == null ? DEFAULT_KEY_SEPARATOR : separator;
    }

    public static RedisKeyUtil of(String prefix, String separator) {
        return new RedisKeyUtil(prefix, separator);
    }

    public RedisKeyUtil secondaryPrefix(String... secondaryPrefix) {
        this.secondaryPrefix = String.join(separator, secondaryPrefix);
        return this;
    }

    /**
     * 向内部键值映射中注册指定的键及其对应的名称。
     * 如果该键已存在，则不进行任何操作。
     * 生成的键值是基于前缀、次级前缀和提供的名称，通过分隔符拼接而成的。
     * <p>
     * 注意，该方法线程不安全。若多个线程使用单例工具类，则应换用 {@link #registerKeyWithSecPrefix(String, String, String...)}
     *
     * @param key  要注册的键，不允许为 null。
     * @param name 键所对应的名称，可以为 null。如果为 null，仅使用前缀和次级前缀生成键值。
     * @return 返回当前的 RedisKeyUtil 实例，以支持链式调用。
     */
    public RedisKeyUtil registerKey(String key, String name) {
        keyMap.computeIfAbsent(key, k -> Stream.of(prefix, secondaryPrefix, name)
                                               .filter(s -> s != null && !s.isBlank())
                                               .collect(Collectors.joining(separator)));
        return this;
    }

    /**
     * 向内部键值映射中注册指定的键及其对应的名称，并允许指定次级前缀。
     * 如果该键已存在，则不进行任何操作。
     * 生成的键值是基于主要前缀、提供的次级前缀和名称，通过分隔符拼接而成的。
     * <p>
     * 若工具实例在多个线程中使用，则应使用该方法以确保不会因并发问题而有意外的次级前缀
     *
     * @param key             要注册的键，不允许为 null。
     * @param name            键所对应的名称，可以为 null。如果为 null，仅使用主要前缀与次级前缀生成键值。
     * @param secondaryPrefix 可选的次级前缀。如果为 null，仅使用主要前缀与名称生成键值。
     * @return 返回当前的 RedisKeyUtil 实例，以支持链式调用。
     */
    public RedisKeyUtil registerKeyWithSecPrefix(String key, String name, String... secondaryPrefix) {
        String combinedSecPrefix = Arrays.stream(secondaryPrefix)
                                         .filter(s -> s != null && !s.isBlank())
                                         .collect(Collectors.joining(separator));

        keyMap.computeIfAbsent(key, k -> Stream.of(prefix, combinedSecPrefix, name)
                                               .filter(s -> s != null && !s.isBlank())
                                               .collect(Collectors.joining(separator)));
        return this;
    }

    /**
     * 使用指定的完整前缀，向内部键值映射中注册指定的键及其对应的名称。
     * 如果该键已存在，则不进行任何操作。
     * 生成的键值是基于提供的完整前缀和名称，通过分隔符拼接而成的。
     * <p>
     * 若工具实例在多个线程中使用，则应使用该方法以确保不会因并发问题而有意外的次级前缀
     *
     * @param key    要注册的键，不允许为 null。
     * @param name   键所对应的名称，可以为 null。如果为 null，仅使用主要前缀与次级前缀生成键值。
     * @param prefix 指定的完整前缀。
     * @return 返回当前的 RedisKeyUtil 实例，以支持链式调用。
     */
    public RedisKeyUtil registerKeyWithPrefix(String key, String name, String prefix) {
        keyMap.computeIfAbsent(key, k -> Stream.of(prefix, name)
                                               .filter(s -> s != null && !s.isBlank())
                                               .collect(Collectors.joining(separator)));
        return this;
    }


    public RedisKeyUtil registerKey(String key) {
        return registerKey(key, null);
    }

    public String getKey(String key) {
        return keyMap.get(key);
    }

    public Set<String> getKeys() {
        return keyMap.keySet();
    }

    public void clear() {
        keyMap.clear();
    }

    public String removeKey(String key) {
        return keyMap.remove(key);
    }

    public Map<String, String> getKeyMap() {
        return keyMap;
    }

}

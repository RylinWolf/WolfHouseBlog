package com.wolfhouse.wolfhouseblog.common.utils.redisutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RedisKey 是一个用于标记字段、类、方法或参数的注解，旨在帮助生成和管理 Redis 键。
 * 使用此注解可以自定义 Redis 键的前缀、分隔符及其他规则。
 * <p>
 * 修饰字段时，遵循以下规则：
 * - 字段值作为 key
 * - 若指定 asName 为 true，则 Redis 的键 为 [RedisKey(注解).prefix + secondPrefix] + [以 _ 分隔的小写字段名]，此时 name 属性将不生效
 *
 * @author Rylin Wolf
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisKey {
    /** 默认前缀 */
    String prefix() default RedisKeyUtil.DEFAULT_KEY_PREFIX;

    /** 二级前缀 */
    String secondPrefix() default "";

    /** 分隔符 */
    String separator() default RedisKeyUtil.DEFAULT_KEY_SEPARATOR;

    /** 自定义名称，asName 为 true 时，跟在字段名之后 */
    String name() default "";

    /**
     * 是否将字段名 作为 Redis 键结尾
     * <p>
     * 对类使用时，该属性不生效
     */
    boolean asName() default true;

    /**
     * 注解的类是否为键常量类，若是，则提取所有常量，遵从以下规范：
     * <p>
     * 常量名以下划线分隔后转小写，通过分隔符拼接后作为 Redis 键。
     * 如 USER_LIST 最终的 Redis 键为 [prefix]:[secondPrefix]:user:list
     * <p>
     * 常量值作为 RedisKeyUtil 的 key，用于获取 Redis 键。
     * 如常量 USER_LIST = "userList"，
     * 则可以通过 getKey("userList") 得到 [prefix]:[secondPrefix]:user:list
     */
    boolean isKeysConstant() default false;
}

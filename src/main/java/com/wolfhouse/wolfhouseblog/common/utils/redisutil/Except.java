package com.wolfhouse.wolfhouseblog.common.utils.redisutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配合 RedisKey 注解使用，用于标注该字段不应被作为 Redis Key
 *
 * @author Rylin Wolf
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Except {
}

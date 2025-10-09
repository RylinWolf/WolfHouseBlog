package com.wolfhouse.wolfhouseblog.common.constant.redis;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author linexsong
 */
public class RedisConstant {
    public static final String SERVICE = "service";
    public static final String SEPARATOR = ":";
    public static final String LOCK = "lock";
    public static final Object BLOCK_LOCK = "block-lock";

    public static final String INSERT_FAILED = "缓存添加失败！";

    public static String format(String... path) {
        return format(true, path);
    }

    public static String format(Boolean root, String... path) {
        return root ? Stream.concat(Stream.of(SERVICE), Arrays.stream(path))
                            .collect(Collectors.joining(SEPARATOR)) : String.join(SEPARATOR, path);
    }
}

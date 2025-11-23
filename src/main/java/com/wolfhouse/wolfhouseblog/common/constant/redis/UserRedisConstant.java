package com.wolfhouse.wolfhouseblog.common.constant.redis;

import java.time.Duration;

/**
 * @author linexsong
 */
public class UserRedisConstant {
    public static final String USER = "user";
    public static final String BASE = RedisConstant.format(USER);

    /** 权限字段 */
    public static final String AUTHORITIES =
        RedisConstant.format(false, BASE, "authorities") + RedisConstant.SEPARATOR + "%d";

    /** Token */
    public static final String TOKEN =
        RedisConstant.format(false, BASE, "token") + RedisConstant.SEPARATOR + "%s";

    /** 用户信息 */
    public static final String INFO = RedisConstant.format(false, BASE, "info") + RedisConstant.SEPARATOR + "%s";
    /** 用户信息缓存时间 */
    public static final Long INFO_TIMEOUT_MINUTES = 3 * 24 * 60L;

    /** 头像指纹 */
    public static final String AVATAR_FINGERPRINT =
        RedisConstant.format(false, BASE, "avatar") + RedisConstant.SEPARATOR + "%s";

    /** 头像过期时间 */
    public static final Duration AVATAR_DURATION = Duration.ofMinutes(60);
}

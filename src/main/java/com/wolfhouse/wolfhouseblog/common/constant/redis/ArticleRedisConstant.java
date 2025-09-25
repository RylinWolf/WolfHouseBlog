package com.wolfhouse.wolfhouseblog.common.constant.redis;

/**
 * @author linexsong
 */
public class ArticleRedisConstant {
    public static final String ARTICLE = "article";
    public static final String BASE = RedisConstant.format(ARTICLE);
    public static final Long LOCK_TIME_SECONDS = 10L;

    /** 查询文章缩略列表 */
    public static final String QUERY_BRIEF =
        RedisConstant.format(false, BASE, "brief") + RedisConstant.SEPARATOR + "%s";

    public static final String VO = RedisConstant.format(false, BASE, "vo") + RedisConstant.SEPARATOR + "%s";
    public static final String VO_LOCK = RedisConstant.format(false, BASE, "vo", RedisConstant.LOCK) + "%s";

    public static final String VIEWS = RedisConstant.format(false, BASE, "views") + RedisConstant.SEPARATOR + "%s";
    public static final String VIEWS_LOCK = RedisConstant.format(false, BASE, "views", RedisConstant.LOCK);
    public static final Long VIEWS_EXPIRE_MINUTES = 130L;
}

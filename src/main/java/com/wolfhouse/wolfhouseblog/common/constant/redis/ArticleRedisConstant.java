package com.wolfhouse.wolfhouseblog.common.constant.redis;

/**
 * @author linexsong
 */
public class ArticleRedisConstant {
    public static final String ARTICLE = "article";
    public static final String BASE = RedisConstant.format(ARTICLE);

    /** 查询文章缩略列表 */
    public static final String QUERY_BRIEF =
        RedisConstant.format(false, BASE, "brief") + RedisConstant.SEPARATOR + "%s";

    public static final String VO = RedisConstant.format(false, BASE, "vo") + RedisConstant.SEPARATOR + "%s";

    public static final String VIEW = RedisConstant.format(false, BASE, "view") + RedisConstant.SEPARATOR + "%s";
}

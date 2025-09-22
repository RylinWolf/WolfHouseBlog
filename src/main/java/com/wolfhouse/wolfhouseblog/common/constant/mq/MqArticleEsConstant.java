package com.wolfhouse.wolfhouseblog.common.constant.mq;

/**
 * @author linexsong
 */
public class MqArticleEsConstant {
    public static final String ARTICLE = MqArticleConstant.ARTICLE;
    public static final String ES = "es";
    public static final String BASE =
        MqConstant.SERVICE + MqConstant.SEPARATOR + MqArticleEsConstant.ES + MqConstant.SEPARATOR + ARTICLE +
        MqConstant.SEPARATOR;

    public static final String POST = "post";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";

    public static final String POST_EXCHANGE = BASE + POST + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    public static final String POST_QUEUE = BASE + POST + MqConstant.SEPARATOR + MqConstant.QUEUE;
    public static final String POST_KEY = BASE + POST + MqConstant.SEPARATOR + MqConstant.KEY;

    public static final String DELETE_EXCHANGE = BASE + DELETE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    public static final String DELETE_QUEUE = BASE + DELETE + MqConstant.SEPARATOR + MqConstant.QUEUE;
    public static final String DELETE_KEY = BASE + DELETE + MqConstant.SEPARATOR + MqConstant.KEY;

    public static final String UPDATE_EXCHANGE = BASE + UPDATE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    public static final String UPDATE_QUEUE = BASE + UPDATE + MqConstant.SEPARATOR + MqConstant.QUEUE;
    public static final String UPDATE_KEY = BASE + UPDATE + MqConstant.SEPARATOR + MqConstant.KEY;


}

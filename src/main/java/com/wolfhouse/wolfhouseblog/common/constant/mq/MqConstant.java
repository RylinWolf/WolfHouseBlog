package com.wolfhouse.wolfhouseblog.common.constant.mq;

/**
 * @author linexsong
 */
public class MqConstant {
    public static final String SEPARATOR = ".";
    public static final String ONCE_WILDCARD = "*";
    public static final String MULTI_WILDCARD = "#";

    public static final String SERVICE = "service";
    public static final String QUEUE = "queue";
    public static final String EXCHANGE = "exchange";
    public static final String KEY = "key";
    public static final String LAZY_ARG = "x-queue-mode";
    public static final String QUEUE_MODE_LAZY = "lazy";


    public static final String ERROR = "error";
    public static final String ERROR_QUEUE = "error.queue";
    public static final String ERROR_EXCHANGE = "error.exchange";

    public static final String USER_ERROR_QUEUE = MqUserConstant.USER + ERROR_QUEUE;
    public static final String ARTICLE_ERROR_QUEUE = MqArticleConstant.ARTICLE + ERROR_QUEUE;

    public static final String EXCEPTION_SERVICE = "业务异常";
    public static final String CONCURRENCY = "5";
}

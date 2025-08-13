package com.wolfhouse.wolfhouseblog.common.constant.mq;

/**
 * @author linexsong
 */
public class MqArticleConstant {
    public static final String ARTICLE = "article";

    public static final String PARTITION_CHANGE = "partitionChange";

    public static final String PARTITION_CHANGE_EXCHANGE =
         PARTITION_CHANGE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;

    public static final String PARTITION_CHANGE_QUEUE =
         PARTITION_CHANGE + MqConstant.SEPARATOR + MqConstant.QUEUE;

    public static final String PARTITION_CHANGE_KEY =
         PARTITION_CHANGE + MqConstant.SEPARATOR + MqConstant.KEY;
}

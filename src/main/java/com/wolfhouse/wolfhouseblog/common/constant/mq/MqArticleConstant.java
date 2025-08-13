package com.wolfhouse.wolfhouseblog.common.constant.mq;

/**
 * @author linexsong
 */
public class MqArticleConstant {
    public static final String ARTICLE = "article";

    public static final String BASE = MqConstant.SERVICE + MqConstant.SEPARATOR + ARTICLE + MqConstant.SEPARATOR;

    public static final String PARTITION = "partition";

    public static final String CHANGE = "change";

    public static final String PARTITION_CHANGE_EXCHANGE =
         BASE + PARTITION + CHANGE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;

    public static final String PARTITION_CHANGE_QUEUE =
         BASE + PARTITION + CHANGE + MqConstant.SEPARATOR + MqConstant.QUEUE;

    public static final String KEY_PARTITION_CHANGE =
         BASE + PARTITION + CHANGE + MqConstant.SEPARATOR + MqConstant.KEY;
}

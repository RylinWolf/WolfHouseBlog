package com.wolfhouse.wolfhouseblog.common.constant.mq;

/**
 * @author linexsong
 */
public class MqUserConstant {
    public static final String USER = "user";
    public static final String BASE =
            MqConstant.SERVICE + MqConstant.SEPARATOR + MqUserConstant.USER + MqConstant.SEPARATOR;


    public static final String CREATE = "create";
    public static final String CREATE_QUEUE = BASE + CREATE + MqConstant.SEPARATOR + MqConstant.QUEUE;
    public static final String CREATE_EXCHANGE = BASE + CREATE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
}

package com.wolfhouse.wolfhouseblog.common.constant.mq;

import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;

/**
 * @author linexsong
 */
public class MqUserConstant {
    public static final String USER = UserConstant.USER;
    public static final String BASE =
         MqConstant.SERVICE + MqConstant.SEPARATOR + MqUserConstant.USER + MqConstant.SEPARATOR;


    public static final String CREATE = "create";
    public static final String DELETE = "delete";
    public static final String DISABLE = "disable";

    public static final String CREATE_QUEUE = BASE + CREATE + MqConstant.SEPARATOR + MqConstant.QUEUE;
    public static final String CREATE_EXCHANGE = BASE + CREATE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    public static final String KEY_CREATE_USER = BASE + CREATE + MqConstant.SEPARATOR + MqConstant.KEY;

    public static final String DELETE_EXCHANGE = BASE + DELETE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    public static final String KEY_DELETE_USER = BASE + DELETE + MqConstant.SEPARATOR + MqConstant.KEY;
    public static final String DELETE_QUEUE = BASE + DELETE + MqConstant.SEPARATOR + MqConstant.QUEUE;

    public static final String DISABLE_EXCHANGE = BASE + DISABLE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    public static final String DISABLE_KEY = BASE + DISABLE + MqConstant.SEPARATOR + MqConstant.KEY;
    public static final String DISABLE_QUEUE = BASE + DISABLE + MqConstant.SEPARATOR + MqConstant.QUEUE;
}

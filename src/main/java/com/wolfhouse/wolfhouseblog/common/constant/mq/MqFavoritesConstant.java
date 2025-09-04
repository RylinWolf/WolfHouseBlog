package com.wolfhouse.wolfhouseblog.common.constant.mq;

/**
 * @author linexsong
 */
public class MqFavoritesConstant {
    public static final String FAVORITES = "favorites";
    public static final String BASE = MqConstant.SERVICE + MqConstant.SEPARATOR + FAVORITES + MqConstant.SEPARATOR;

    public static final String INIT = "init";

    public static final String INIT_QUEUE = BASE + INIT + MqConstant.SEPARATOR + MqConstant.QUEUE;
    public static final String INIT_EXCHANGE = BASE + INIT + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    public static final String INIT_KEY = BASE + INIT + MqConstant.SEPARATOR + MqConstant.KEY;
}

package com.wolfhouse.wolfhouseblog.common.constant.mq;

/**
 * @author linexsong
 */
public class MqArticleConstant {
    public static final String ARTICLE = "article";

    public static final String BASE = MqConstant.SERVICE + MqConstant.SEPARATOR + ARTICLE + MqConstant.SEPARATOR;

    public static final String VIEW = "view";
    public static final String PARTITION = "partition";
    public static final String TAG = "tag";
    public static final String FAVORITES = "favorites";

    public static final String CHANGE = "change";
    public static final String REMOVE = "remove";

    /** 改变分区交换机 */
    public static final String PARTITION_CHANGE_EXCHANGE =
        BASE + PARTITION + CHANGE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    /** 移除标签交换机 */
    public static final String TAG_REMOVE_EXCHANGE = BASE + TAG + REMOVE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    /** 移除收藏夹交换机 */
    public static final String FAVORITES_REMOVE_EXCHANGE =
        BASE + FAVORITES + REMOVE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;
    /** 文章修改交换机 */
    public static final String ARTICLE_CHANGE_EXCHANGE = BASE + CHANGE + MqConstant.SEPARATOR + MqConstant.EXCHANGE;


    /** 改变分区队列 */
    public static final String PARTITION_CHANGE_QUEUE =
        BASE + PARTITION + CHANGE + MqConstant.SEPARATOR + MqConstant.QUEUE;
    /** 移除标签队列 */
    public static final String TAG_REMOVE_QUEUE = BASE + TAG + REMOVE + MqConstant.SEPARATOR + MqConstant.QUEUE;

    /** 移除收藏夹队列 */
    public static final String FAVORITES_REMOVE_QUEUE =
        BASE + FAVORITES + REMOVE + MqConstant.SEPARATOR + MqConstant.QUEUE;

    /** 浏览量自增队列 */
    public static final String ARTICLE_CHANGE_VIEW_INCREASE_QUEUE =
        BASE + VIEW + MqConstant.SEPARATOR + MqConstant.QUEUE;

    /** 改变分区 KEY */
    public static final String KEY_PARTITION_CHANGE =
        BASE + PARTITION + CHANGE + MqConstant.SEPARATOR + MqConstant.KEY;
    /** 移除标签 KEY */
    public static final String KEY_TAG_REMOVE = BASE + TAG + REMOVE + MqConstant.SEPARATOR + MqConstant.KEY;

    /** 移除收藏夹 KEY */
    public static final String KEY_FAVORITES_REMOVE =
        BASE + FAVORITES + REMOVE + MqConstant.SEPARATOR + MqConstant.KEY;

    /** 浏览量自增 KEY */
    public static final String KEY_ARTICLE_VIEW_INCREASE = BASE + VIEW + MqConstant.SEPARATOR + MqConstant.KEY;

}

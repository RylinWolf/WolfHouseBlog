package com.wolfhouse.wolfhouseblog.common.constant.services;

/**
 * @author linexsong
 */
public class UserConstant {
    /** 默认用户账号随机数字部分长度 */
    public static final Integer DEFAULT_ACCOUNT_CODE_LEN = 6;
    public static final String ACCOUNT_SEPARATOR = "#";


    public static final String USER = "user";
    public static final String BIRTH = "birth";


    public static final String USER_AUTH_CREATE_FAILED = "用户创建失败";
    public static final String USER_NOT_EXIST = "用户不存在！";
    public static final String USER_ALREADY_EXIST = "用户已存在！";
    public static final String USER_UPDATE_FAILED = "用户修改失败！";
    public static final String VERIFY_FAILED = "字段验证失败！";
    public static final String SUBSCRIBE_FAILED = "关注失败！";
    public static final String USER_HAS_BEEN_BANNED = "账号被停用";
    public static final String USER_UNACCESSIBLE = "无法访问该用户";
    public static final String USER_ALREADY_SUBSCRIBED = "已关注该用户";
}

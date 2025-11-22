package com.wolfhouse.wolfhouseblog.common.constant.services;

import java.util.Set;

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
    public static final String DELETE_FAILED = "账号删除失败！";
    public static final String DISABLE_FAILED = "账号禁用失败！";
    public static final String USER_HAS_ENABLED = "账户已启用！";
    public static final String ENABLE_FAILED = "账号启用失败！";
    public static final String UNSUBSCRIBE_FAILED = "取消关注失败！";
    public static final String SUBSCRIBE_CANNOT_BE_SELF = "不能关注自己！";
    public static final String NOT_SUBSCRIBED = "未关注该用户！";
    public static final String EMAIL_FORMAT_NOT_SUPPORT = "邮箱格式不正确！";
    public static final String MAIL_CODE_UNVERIFIED = "邮箱验证码不正确";

    public static final Set<String> ALLOWED_AVATAR_TYPE = Set.of("png", "jpg", "jpeg");
    public static final String AVATAR_VALID_FAILED = "头像验证失败";
    // region 头像常量
    /** 触发压缩阈值的图片大小 0.6 MB */
    public static final long AVATAR_COMPRESS_SIZE = (long) (0.6 * 1024 * 1024);
    /** 压缩质量 0.7 */
    public static final float AVATAR_COMPRESS_QUALITY = 0.7f;
    /** 最大宽度 */
    public static final int AVATAR_MAX_WIDTH = 1024;
    /** 最大高度 */
    public static final int AVATAR_MAX_HEIGHT = 1024;
    /** 转换格式 */
    public static final String AVATAR_FORMAT = "jpg";
    // endregion
}

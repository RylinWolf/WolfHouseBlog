package com.wolfhouse.wolfhouseblog.common.utils.result;

/**
 * Http 状态封装类
 *
 * @author Rylin Wolf
 */
public enum HttpCode {
    /** 成功 */
    SUCCESS(20000, ""),
    BAD_REQUEST(40000, "请求错误"),
    PARAM_ERROR(40010, "参数错误"),
    UN_AUTHORIZED(40100, "未登录"),
    FORBIDDEN(40300, "访问被拒绝"),
    NO_PERMISSION(40311, "无操作权限"),
    MEDIA_TYPE_NOT_ACCEPTABLE(40600, "不可接受的媒体类型"),
    UNSUPPORTED_MEDIA_TYPE(41500, "不支持的媒体类型"),
    SQL_ERROR(50011, "SQL 错误"),
    IO_ERROR(50021, "IO 错误"),
    OSS_UPLOAD_FAILED(50031, "OSS 上传出错"),
    UNKNOWN(50099, "未知错误"),
    ;
    public final int code;
    public final String message;

    HttpCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpCode ofCode(int code) {
        for (HttpCode httpCode : HttpCode.values()) {
            if (httpCode.code == code) {
                return httpCode;
            }
        }
        return UNKNOWN;
    }
}

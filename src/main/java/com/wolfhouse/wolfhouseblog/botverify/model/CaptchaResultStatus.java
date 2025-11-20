package com.wolfhouse.wolfhouseblog.botverify.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.EnumValue;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;

/**
 * 人机验证请求结果状态码枚举
 *
 * @author Rylin Wolf
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CaptchaResultStatus {
    /** 成功 */
    SUCCESS(200, "Success", "成功"),

    /** 失败 */
    MISSING_PARAMETER(400, "MissingParameter", "缺少必要参数"),
    INVALID_PARAMETER(401, "InvalidParameter", "参数不合法"),
    ACCOUNT_ACCESS_DENIED(403, "Forbidden.AccountAccessDenied", "无权限，检查是否开通或已欠费"),
    RAM_ACCESS_DENIED(403, "Forbidden.Forbidden.AccountAccessDenied", "RAM 用户无权限"),
    INTERNAL_ERROR(500, "InternalError", "验证系统内部错误，建议重试");


    public final Integer statusCode;
    @EnumValue
    public final String code;
    public final String msg;

    CaptchaResultStatus(Integer statusCode, String code, String msg) {
        this.statusCode = statusCode;
        this.code = code;
        this.msg = msg;
    }

    public static CaptchaResultStatus of(String code) {
        for (CaptchaResultStatus status : CaptchaResultStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new ServiceException(ServiceExceptionConstant.PARAM_ERROR);
    }
}

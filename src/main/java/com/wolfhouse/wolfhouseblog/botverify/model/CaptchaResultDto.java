package com.wolfhouse.wolfhouseblog.botverify.model;

import lombok.Data;

/**
 * 人机验证结果封装类, 示例数据：
 * <p>
 * {
 * requestId: 0,
 * success: true,
 * status: {@link com.wolfhouse.wolfhouseblog.botverify.model.CaptchaResultStatus},
 * verifyResult: {@link com.wolfhouse.wolfhouseblog.botverify.model.VerifyResult}
 * }
 *
 * @author Rylin Wolf
 */
@Data
public class CaptchaResultDto {
    /** 请求 ID */
    private String requestId;

    /** 请求是否成功 */
    private Boolean success;

    /**
     * 请求结果，包括状态码、返回码、详细信息
     * <p>
     * {
     * statusCode: 200,
     * code: "success",
     * msg: "成功"
     * }
     */
    private CaptchaResultStatus status;

    /**
     * 验证结果
     * <p>
     * {
     * verifyResult: true,
     * verifyCode: "服务端校验通过",
     * certifyId: "xxx"
     * }
     */
    private VerifyResult verifyResult;


}

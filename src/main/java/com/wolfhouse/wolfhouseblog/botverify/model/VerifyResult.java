package com.wolfhouse.wolfhouseblog.botverify.model;

import lombok.Data;

/**
 * @author Rylin Wolf
 */
@Data
public class VerifyResult {
    private Boolean verifyResult;
    private VerifyResultCode verifyCode;

    /**
     * 若初始化传入自定义CertifyID参数（UserCertifyId），
     * 该值将透传自定义CertifyID，支持客户关联校验。
     * <p>
     * 若初始化未传入自定义CertifyID参数，
     * 该值将展示服务端生成的默认CertifyID，用于标识验证码单次验证周期。
     */
    private String certifyId;
}

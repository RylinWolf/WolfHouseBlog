package com.wolfhouse.wolfhouseblog.botverify;

import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponseBody;
import com.wolfhouse.wolfhouseblog.botverify.model.CaptchaResultDto;
import com.wolfhouse.wolfhouseblog.botverify.model.CaptchaResultStatus;
import com.wolfhouse.wolfhouseblog.botverify.model.VerifyResult;
import com.wolfhouse.wolfhouseblog.botverify.model.VerifyResultCode;

/**
 * @author Rylin Wolf
 */
public class AliCaptchaUtils {
    /**
     * 从人机校验响应中提取验证结果并封装到CaptchaResultDto对象中。
     *
     * @param response 人机校验响应对象
     * @return 封装了人机校验结果的状态和其他信息的CaptchaResultDto对象
     */
    public static CaptchaResultDto extractResult(VerifyIntelligentCaptchaResponse response) {
        // 1. 初始化人机校验结果 Dto
        CaptchaResultDto result = new CaptchaResultDto();
        VerifyResult verifyResult = new VerifyResult();
        result.setVerifyResult(verifyResult);

        // 获取响应体内容
        VerifyIntelligentCaptchaResponseBody body = response.getBody();

        // 设置请求成功状态
        result.setSuccess(body.getSuccess());

        // 设置请求 ID
        result.setRequestId(body.getRequestId());

        // 2. 根据返回码获得请求状态
        CaptchaResultStatus resultStatus = CaptchaResultStatus.of(body.getCode());
        result.setStatus(resultStatus);
        // 请求不成功，则不解构验证结果
        if (!CaptchaResultStatus.SUCCESS.equals(resultStatus)) {
            return result;
        }
        // 提取验证结果
        VerifyIntelligentCaptchaResponseBody.VerifyIntelligentCaptchaResponseBodyResult verifyResponseResult =
            body.getResult();

        // 3. 构建验证结果
        verifyResult.setCertifyId(verifyResponseResult.getCertifyId());
        verifyResult.setVerifyResult(verifyResponseResult.getVerifyResult());
        verifyResult.setVerifyCode(VerifyResultCode.of(verifyResponseResult.getVerifyCode()));

        return result;
    }
}

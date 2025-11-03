package com.wolfhouse.wolfhouseblog.botverify;

import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.wolfhouse.wolfhouseblog.botverify.model.CaptchaResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Rylin Wolf
 */
@Service
@RequiredArgsConstructor
public class AliCaptchaServiceImpl implements CaptchaService {
    private final Client captchaClient;

    @Override
    public CaptchaResultDto doCaptcha(String captchaParam) throws Exception {
        // 构建请求体
        VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest();
        request.setSceneId(AliCaptchaConfig.SCENE_ID);
        request.setCaptchaVerifyParam(captchaParam);
        // 发送验证请求
        VerifyIntelligentCaptchaResponse response = captchaClient.verifyIntelligentCaptcha(request);
        // 解构响应并返回
        return AliCaptchaUtils.extractResult(response);
    }
}

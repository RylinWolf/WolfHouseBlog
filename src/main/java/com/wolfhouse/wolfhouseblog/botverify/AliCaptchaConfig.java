package com.wolfhouse.wolfhouseblog.botverify;

import com.aliyun.captcha20230305.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rylin Wolf
 */
@Configuration
public class AliCaptchaConfig {
    private final String accessKeyId = System.getenv("ALI_CLOUD_VERIFY_ID");
    private final String accessSecret = System.getenv("ALI_CLOUD_VERIFY_SECRET");
    public final static String SCENE_ID = System.getenv("ALI_CLOUD_VERIFY_SCENE_ID");

    @Bean
    public com.aliyun.captcha20230305.Client captchaClient() throws Exception {
        Config config = new Config();
        config.setAccessKeyId(accessKeyId);
        config.setAccessKeySecret(accessSecret);
        config.endpoint = "captcha-dualstack.cn-shanghai.aliyuncs.com";
        config.setRegionId("cn");
        return new Client(config);
    }
}

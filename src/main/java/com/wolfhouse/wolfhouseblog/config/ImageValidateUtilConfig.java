package com.wolfhouse.wolfhouseblog.config;

import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.utils.imageutil.ImgValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rylin Wolf
 */
@Configuration
public class ImageValidateUtilConfig {
    @Bean
    public ImgValidator avatarValidator() {
        return new ImgValidator(
            // 最大 1.2 MB
            (long) (1.2 * 1024 * 1024),
            // 宽度 2048
            2048,
            // 高度 2048
            2048,
            // 最大像素 200 万
            200_000_000,
            // 允许类型
            UserConstant.ALLOWED_AVATAR_TYPE,
            // 媒体类型前缀
            "image/");
    }
}

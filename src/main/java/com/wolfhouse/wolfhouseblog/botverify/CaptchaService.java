package com.wolfhouse.wolfhouseblog.botverify;

import com.wolfhouse.wolfhouseblog.botverify.model.CaptchaResultDto;

/**
 * 人机验证服务类
 *
 * @author Rylin Wolf
 */
public interface CaptchaService {
    /**
     * 进行验证操作
     *
     * @param captchaParam 验证服务器传来的验证参数，不得修改
     * @return 是否验证成功
     */
    CaptchaResultDto doCaptcha(final String captchaParam) throws Exception;
}

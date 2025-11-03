package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.botverify.CaptchaService;
import com.wolfhouse.wolfhouseblog.botverify.model.CaptchaResultDto;
import com.wolfhouse.wolfhouseblog.botverify.model.VerifyResult;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.mail.MailService;
import com.wolfhouse.wolfhouseblog.pojo.dto.MailRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.MailRegisterVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rylin Wolf
 */
@Slf4j
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
@Tag(name = "邮件接口")
public class MailController {
    private final MailService mailService;
    private final CaptchaService captchaService;

    @PostMapping("/register")
    @Operation(description = "发送注册验证码")
    public HttpResult<MailRegisterVo> sendCode(@RequestBody @Valid MailRegisterDto registerDto) {
        MailRegisterVo vo = new MailRegisterVo();
        // 进行验证
        try {
            // 获取验证结果
            CaptchaResultDto captchaResult = captchaService.doCaptcha(registerDto.getVerifyParams());
            VerifyResult verifyResult = captchaResult.getVerifyResult();
            // 注入验证结果
            vo.setVerifyResult(verifyResult);
            Boolean isVerified = verifyResult.getVerifyResult();
            // 验证不通过，返回响应
            if (!isVerified) {
                return HttpResult.failed(HttpCodeConstant.VERIFY_FAILED,
                                         AuthExceptionConstant.AUTHENTIC_FAILED,
                                         vo);
            }
        } catch (Exception e) {
            log.error("人机验证失败", e);
            return HttpResult.failed(HttpCodeConstant.SERVER_ERROR,
                                     ServiceExceptionConstant.EXEC_FAILED,
                                     vo);
        }

        // 验证通过，发送验证码
        try {
            mailService.sendCode(registerDto.getEmail());
        } catch (MessagingException e) {
            log.error("验证码邮件发送失败", e);
            return HttpResult.failed(HttpCodeConstant.SERVER_ERROR,
                                     ServiceExceptionConstant.EXEC_FAILED,
                                     vo);
        }
        return HttpResult.success(vo);
    }
}

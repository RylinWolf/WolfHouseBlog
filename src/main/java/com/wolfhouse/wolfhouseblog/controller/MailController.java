package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.mail.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/register")
    @Operation(description = "发送注册验证码")
    public HttpResult<?> sendCode(@Email String email) {
        try {
            mailService.sendCode(email);
        } catch (MessagingException e) {
            log.error("验证码邮件发送失败", e);
            return HttpResult.failed(ServiceExceptionConstant.EXEC_FAILED);
        }
        return HttpResult.success();
    }
}

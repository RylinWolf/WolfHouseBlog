package com.wolfhouse.wolfhouseblog.pojo.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * 邮件注册 Dto
 *
 * @author Rylin Wolf
 */
@Data
public class MailRegisterDto {
    /** 注册的邮箱 */
    @Email
    private String email;
    /** 用于二次人机验证的验证参数 */
    private String verifyParams;
}

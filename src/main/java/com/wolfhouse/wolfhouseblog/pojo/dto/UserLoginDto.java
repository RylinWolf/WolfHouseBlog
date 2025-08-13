package com.wolfhouse.wolfhouseblog.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户登录 Dto")
@Data
public class UserLoginDto {
    @Schema(description = "账号（或邮箱）")
    private String account;
    @Schema(description = "密码")
    private String password;
}

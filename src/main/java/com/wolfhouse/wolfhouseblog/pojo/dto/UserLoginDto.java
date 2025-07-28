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
@Schema(name = "用户登录 Dto")
@Data
public class UserLoginDto {
    @Schema(name = "账号（或邮箱）")
    private String account;
    private String password;
}

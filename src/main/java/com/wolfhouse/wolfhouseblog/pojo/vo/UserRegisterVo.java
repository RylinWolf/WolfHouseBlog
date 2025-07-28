package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@Data
public class UserRegisterVo {
    private String token;
    private String account;
    private String username;
    private String email;
}

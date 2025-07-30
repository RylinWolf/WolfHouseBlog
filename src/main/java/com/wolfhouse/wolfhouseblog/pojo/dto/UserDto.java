package com.wolfhouse.wolfhouseblog.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author linexsong
 */
@Component
@Data
public class UserDto {
    private String avatar;
    @Size(max = 50)
    private String personalStatus;
    @Size(min = 2, max = 20)
    private String nickname;
    @Size(max = 20)
    private String phone;
    @Email
    private String email;
    private Date birth;
}

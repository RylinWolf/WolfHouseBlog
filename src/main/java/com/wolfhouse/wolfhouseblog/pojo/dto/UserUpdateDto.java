package com.wolfhouse.wolfhouseblog.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author linexsong
 */
@Component
@Data
public class UserUpdateDto {
    private String avatar;
    @Size(max = 50)
    private String personalStatus;
    @Size(max = 20)
    private String nickname;
    @Size(min = 11, max = 20)
    private String phone;
    @Email
    private String email;
    private LocalDate birth;
}

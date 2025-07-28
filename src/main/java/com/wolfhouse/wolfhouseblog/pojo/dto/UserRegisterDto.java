package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDto {
    private Long userId;
    
    @NotBlank
    @Size(min = 6, max = 20, message = ServiceExceptionConstant.ARG_FORMAT_ERROR)
    private String password;

    @NotBlank
    @Size(min = 2, max = 20, message = ServiceExceptionConstant.ARG_FORMAT_ERROR)
    @Pattern(regexp = "[a-zA-Z0-9]+", message = ServiceExceptionConstant.ARG_FORMAT_ERROR)
    private String username;

    @NotBlank
    @Email
    private String email;
}

package com.wolfhouse.wolfhouseblog.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Data
@Component
public class UserSubDto {
    private Long fromUser;
    @NotNull
    private Long toUser;
}

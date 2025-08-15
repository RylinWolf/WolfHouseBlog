package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Data
@Component
public class AdminUserControlDto {
    private Long userId;
    private String password;
}

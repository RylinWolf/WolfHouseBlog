package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@Data
public class AdminPostDto {
    private String name;
    private Long userId;
}

package com.wolfhouse.wolfhouseblog.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class TagDto {
    @NotNull
    @Size(min = 1, max = 10)
    private String name;
}

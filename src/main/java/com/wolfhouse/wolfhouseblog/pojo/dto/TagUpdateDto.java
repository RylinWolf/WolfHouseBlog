package com.wolfhouse.wolfhouseblog.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class TagUpdateDto {
    @NotNull
    private Long id;

    @NotNull
    @Size(min = 1, max = 20)
    private String name;
}

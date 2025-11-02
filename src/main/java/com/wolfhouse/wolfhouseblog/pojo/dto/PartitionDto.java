package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class PartitionDto {
    @NotNull
    @Size(min = 1, max = 20)
    private String name;
    private Long parentId;
    private VisibilityEnum visibility;
    private Long order;
}

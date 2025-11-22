package com.wolfhouse.wolfhouseblog.common.utils.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Rylin Wolf
 */
@Data
@Schema(title = "分页查询 DTO")
public class PageQueryDto {
    @Schema(title = "页数")
    protected Long pageNum = 1L;
    @Schema(title = "每页大小")
    protected Long pageSize = 10L;
}

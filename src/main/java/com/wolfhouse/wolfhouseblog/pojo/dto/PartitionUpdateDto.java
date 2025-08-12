package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Data
@Component
public class PartitionUpdateDto {
    private Long id;
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<Long> parentId = JsonNullable.undefined();
    private JsonNullable<VisibilityEnum> visibility = JsonNullable.undefined();
    private JsonNullable<Long> order = JsonNullable.undefined();
}

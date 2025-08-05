package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linexsong
 */
@Data
@Component
public class ArticleUpdateDto {
    private Long id;
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<String> primary = JsonNullable.undefined();
    private JsonNullable<String> content = JsonNullable.undefined();
    private JsonNullable<VisibilityEnum> visibility = JsonNullable.undefined();
    private JsonNullable<Long> partitionId = JsonNullable.undefined();
    private JsonNullable<List<String>> tags = JsonNullable.undefined();
    private JsonNullable<List<Long>> comUseTags = JsonNullable.undefined();
}

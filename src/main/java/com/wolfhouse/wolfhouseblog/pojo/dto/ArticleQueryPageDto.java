package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleQueryPageDto extends PageDto {
    private JsonNullable<Long> id = JsonNullable.undefined();
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<Long> authorId = JsonNullable.undefined();
    private JsonNullable<LocalDateTime> postTime = JsonNullable.undefined();
}

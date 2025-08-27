package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageDto;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentQueryDto extends PageDto {
    private Long articleId;
    private JsonNullable<Long> userId = JsonNullable.undefined();
    private JsonNullable<Long> replyId = JsonNullable.undefined();
}

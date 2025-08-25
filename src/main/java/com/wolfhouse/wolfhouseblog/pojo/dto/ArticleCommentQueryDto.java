package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleCommentQueryDto extends PageDto {
    private Long articleId;
    private JsonNullable<Long> userId;
    private JsonNullable<Long> replyId;
}

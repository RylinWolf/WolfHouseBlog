package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class ArticleCommentDeleteDto {
    private Long articleId;
    private Long commentId;
}

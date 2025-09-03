package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;

/**
 * @author rylinwolf
 */
@Data
public class ArticleCommentDto {
    private Long articleId;
    private Long replyId;
    private String content;
}

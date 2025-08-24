package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;

@Data
public class ArticleCommentDto {
    private Long articleId;
    private Long userId;
    private Long replyId;
    private String content;
}

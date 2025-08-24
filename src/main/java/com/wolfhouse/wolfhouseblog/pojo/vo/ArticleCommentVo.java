package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.Data;

@Data
public class ArticleCommentVo {
    private Long id;
    private Long userId;
    private String content;
    private ArticleCommentVo[] children;
}

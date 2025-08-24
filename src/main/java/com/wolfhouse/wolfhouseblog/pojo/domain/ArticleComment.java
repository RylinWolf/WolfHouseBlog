package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("article_comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleComment {
    private Long id;
    private Long userId;
    private Long articleId;
    private Long replyId;
    private String content;
}

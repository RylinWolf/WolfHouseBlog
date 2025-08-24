package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table("article_like")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLike {
    private Long id;
    private Long userId;
    private Long articleId;
    private LocalDate likeDate;
}

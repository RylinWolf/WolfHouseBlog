package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author rylinwolf
 */
@Table("article_like")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleLike {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long userId;
    private Long articleId;
    private LocalDate likeDate;
}

package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author rylinwolf
 */
@Table("article_favorite")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFavorite {
    private Long id;
    private Long favoriteId;
    private Long userId;
    private LocalDate favoriteDate;
}

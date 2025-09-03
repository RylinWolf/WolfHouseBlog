package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author linexsong
 */
@Data
public class ArticleFavoriteVo {
    private Long articleId;
    private Long favoriteId;
    private LocalDate favoriteDate;
}

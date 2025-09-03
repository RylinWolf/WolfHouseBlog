package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleFavoritePageDto extends PageDto {
    private Long favoritesId;
}

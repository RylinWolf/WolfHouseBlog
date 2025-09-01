package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class FavoritesDto {
    private Long userId;
    private String title;
    private VisibilityEnum visibility;
}

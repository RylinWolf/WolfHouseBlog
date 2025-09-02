package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class FavoritesUpdateDto {
    private Long id;
    private String title;
    private VisibilityEnum visibility;
}

package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.DefaultEnum;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class FavoritesDto {
    private String title;
    private VisibilityEnum visibility;
    private DefaultEnum isDefault;
}

package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.wolfhouse.wolfhouseblog.common.enums.DefaultEnum;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author rylinwolf
 */
@Data
public class FavoritesVo {
    private Long id;
    private String title;
    private VisibilityEnum visibility;
    private DefaultEnum isDefault;
}
package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.DefaultEnum;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoritesDto {
    private String title;
    private VisibilityEnum visibility;
    private DefaultEnum isDefault;
}

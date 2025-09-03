package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.DefaultEnum;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * @author linexsong
 */
@Data
public class FavoritesUpdateDto {
    private Long id;
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<VisibilityEnum> visibility = JsonNullable.undefined();
    private JsonNullable<DefaultEnum> isDefault = JsonNullable.undefined();
}

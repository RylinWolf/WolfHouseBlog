package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wolfhouse.wolfhouseblog.common.enums.DefaultEnum;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author rylinwolf
 */
@Data
@Table("favorites")
public class Favorites {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long userId;
    private String title;
    private VisibilityEnum visibility;
    private DefaultEnum isDefault;
}

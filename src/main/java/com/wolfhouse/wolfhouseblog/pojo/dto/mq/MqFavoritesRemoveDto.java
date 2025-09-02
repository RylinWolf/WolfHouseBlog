package com.wolfhouse.wolfhouseblog.pojo.dto.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MqFavoritesRemoveDto extends MqAuthDto {
    private Long favoritesId;
}

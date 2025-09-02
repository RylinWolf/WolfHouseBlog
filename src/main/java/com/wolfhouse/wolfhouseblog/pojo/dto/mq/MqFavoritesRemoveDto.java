package com.wolfhouse.wolfhouseblog.pojo.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MqFavoritesRemoveDto extends MqAuthDto {
    private Long favoritesId;
}

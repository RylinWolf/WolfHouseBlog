package com.wolfhouse.wolfhouseblog.pojo.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MqPartitionChangeDto extends MqUserAuthDto {
    private Set<Long> oldIds;
    private Long newId;
}

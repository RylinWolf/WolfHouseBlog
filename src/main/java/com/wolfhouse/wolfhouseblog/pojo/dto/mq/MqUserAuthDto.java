package com.wolfhouse.wolfhouseblog.pojo.dto.mq;

import lombok.*;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqUserAuthDto extends MqAuthDto {
    protected Long userId;
}

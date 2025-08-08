package com.wolfhouse.wolfhouseblog.pojo.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author linexsong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqAuthDto {
    protected Long loginId;
    protected List<Long> authorities;
}

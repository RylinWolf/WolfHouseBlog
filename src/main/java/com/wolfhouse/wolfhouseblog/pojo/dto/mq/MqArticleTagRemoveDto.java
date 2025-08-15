package com.wolfhouse.wolfhouseblog.pojo.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author linexsong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqArticleTagRemoveDto {
    private Long userId;
    private Set<Long> tagIds;
}

package com.wolfhouse.wolfhouseblog.pojo.dto.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author linexsong
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MqArticleTagRemoveDto extends MqUserAuthDto {
    private Set<Long> tagIds;

    public MqArticleTagRemoveDto(Long userId, Set<Long> tagIds) {
        super(userId);
        this.tagIds = tagIds;
    }
}

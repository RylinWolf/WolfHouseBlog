package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;

import java.util.Set;

/**
 * @author linexsong
 */
@Data
public class TagDeleteDto {
    private Set<Long> ids;
}

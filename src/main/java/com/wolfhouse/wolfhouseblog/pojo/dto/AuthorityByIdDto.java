package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;

import java.util.Set;

/**
 * @author linexsong
 */
@Data
public class AuthorityByIdDto {
    private Long adminId;
    private Set<Long> ids;
}

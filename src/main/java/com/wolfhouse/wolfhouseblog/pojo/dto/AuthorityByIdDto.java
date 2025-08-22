package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author linexsong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityByIdDto {
    private Long adminId;
    private Set<Long> ids;
}

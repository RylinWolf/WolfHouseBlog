package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityVo {
    private Long id;
    private String permissionName;
}

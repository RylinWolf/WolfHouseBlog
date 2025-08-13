package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author linexsong
 */
@Data
public class AdminVo {
    private Long id;
    private String name;
    private List<AuthorityVo> authorities;
}

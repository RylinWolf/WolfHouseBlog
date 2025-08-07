package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import lombok.Data;

import java.util.List;

/**
 * @author linexsong
 */
@Data
public class AdminVo {
    private String name;
    private List<Authority> authorities;
}

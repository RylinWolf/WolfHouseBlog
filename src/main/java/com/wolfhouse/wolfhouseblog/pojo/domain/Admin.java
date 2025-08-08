package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.List;

/**
 * @author linexsong
 */
@Data
@Table("admin")
public class Admin {
    private String name;
    @Id
    private Long id;
    private Long userId;
    private List<Long> authorities;

}

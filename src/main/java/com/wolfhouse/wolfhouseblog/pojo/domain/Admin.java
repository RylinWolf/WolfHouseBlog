package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.List;

/**
 * @author linexsong
 */
@Data
@Table("admin")
public class Admin {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String name;
    private Long userId;
    private List<Long> authorities;

}

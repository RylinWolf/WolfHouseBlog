package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
@Table("partition")
public class Partition {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long userId;
    private String name;
    private Long parentId;
    private VisibilityEnum visibility;
    private Long order;
}

package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
@Table("partition")
public class Partition {
    private Long id;
    private Long userId;
    private String name;
    private Long parentId;
    private VisibilityEnum visibility;
}

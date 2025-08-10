package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class PartitionVo {
    private Long id;
    private String name;
    private PartitionVo[] children;
    private VisibilityEnum visibility;
}

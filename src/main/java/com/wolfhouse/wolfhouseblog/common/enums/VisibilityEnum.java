package com.wolfhouse.wolfhouseblog.common.enums;

import com.mybatisflex.annotation.EnumValue;

/**
 * @author linexsong
 */
public enum VisibilityEnum {
    /** 公开权限 */
    PUBLIC(0),
    /** 私人权限 */
    PRIVATE(1);

    @EnumValue
    public final Integer value;

    VisibilityEnum(Integer value) {
        this.value = value;
    }
}

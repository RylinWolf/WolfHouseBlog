package com.wolfhouse.wolfhouseblog.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;

/**
 * @author linexsong
 */
public enum DefaultEnum {
    /** 默认值 */
    DEFAULT(1),
    /** 非默认值 */
    NOT_DEFAULT(0);

    @JsonValue
    @EnumValue
    public final Integer value;

    DefaultEnum(Integer value) {
        this.value = value;
    }
}

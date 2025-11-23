package com.wolfhouse.wolfhouseblog.common.enums.oss;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;

/**
 * OSS 文件业务类型
 *
 * @author Rylin Wolf
 */
public enum FileType {
    /** 头像类型 */
    AVATAR("avatar", "头像"),
    ;

    @JsonValue
    @EnumValue
    public final String type;
    public final String desc;

    FileType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}

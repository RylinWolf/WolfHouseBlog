package com.wolfhouse.wolfhouseblog.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author linexsong
 */
@Getter
public enum UserAuthoritiesEnum {
    // 用户管理权限
    USER(10), USER_EDIT(11), USER_DELETE(12), USER_ADD(13),

    // 文章管理权限
    BLOG(20), BLOG_EDIT(21), BLOG_DELETE(22), BLOG_ADD(23),

    // 全部管理权限
    ALL(0);

    @JsonValue
    private final Integer value;

    UserAuthoritiesEnum(Integer value) {
        this.value = value;
    }
}

package com.wolfhouse.wolfhouseblog.common.enums;

import com.mybatisflex.annotation.EnumValue;
import com.wolfhouse.wolfhouseblog.common.constant.services.BlogPermissionConstant;

/**
 * @author linexsong
 */
public enum BlogPermissionEnum {
    /** 超级管理员 */
    SUPER_ADMIN(
         BlogPermissionConstant.ROLE_SUPER_ADMIN,
         BlogPermissionConstant.SUPER_ADMIN_NAME),
    /** 创建用户 */
    USER_CREATE(
         BlogPermissionConstant.USER_CREATE,
         BlogPermissionConstant.USER_CREATE_NAME),
    /** 删除用户 */
    USER_DELETE(
         BlogPermissionConstant.USER_DELETE,
         BlogPermissionConstant.USER_DELETE_NAME),
    /** 修改用户 */
    USER_UPDATE(
         BlogPermissionConstant.USER_UPDATE,
         BlogPermissionConstant.USER_UPDATE_NAME),
    /** 启/停用用户 */
    USER_DISABLE(
         BlogPermissionConstant.USER_DISABLE,
         BlogPermissionConstant.USER_DISABLE_NAME
    );

    @EnumValue
    final String code;
    final String name;

    BlogPermissionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}

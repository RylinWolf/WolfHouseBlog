package com.wolfhouse.wolfhouseblog.common.constant.services;

/**
 * @author linexsong
 */
public class BlogPermissionConstant {
    public static final String ADMIN = "BLOG_ADMIN";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ROLE_" + ADMIN;
    public static final String ROLE_SUPER_ADMIN = "ROLE" + SUPER_ADMIN;

    public static final String ADMIN_NAME = "博客管理员";
    public static final String SUPER_ADMIN_NAME = "超级管理员";

    public static final String USER_CREATE = "blog:user:create";
    public static final String USER_DELETE = "blog:user:delete";
    public static final String USER_UPDATE = "blog:user:update";
    public static final String USER_DISABLE = "blog:user:disable";

    public static final String USER_CREATE_NAME = "添加用户";
    public static final String USER_DELETE_NAME = "删除用户";
    public static final String USER_UPDATE_NAME = "修改用户";
    public static final String USER_DISABLE_NAME = "启/停用用户";

    public static final String AUTHORITY_CREATE = "admin:authority:create";

    public static final String AUTHORITY_CREATE_NAME = "添加管理员权限";
}

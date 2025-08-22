package com.wolfhouse.wolfhouseblog.auth.service;

import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;

/**
 * @author linexsong
 */
public interface ServiceAuthMediator {
    void registerAdmin(AdminService adminService);

    void registerUser(UserService userService);

    void registerUserAuth(UserAuthService userAuthService);

    /**
     * 检查用户认证信息是否存在
     *
     * @param userId 用户ID
     * @return 如果认证信息存在返回true，否则返回false
     */
    Boolean isAuthExist(Long userId);

    /**
     * 检查用户是否已被删除
     *
     * @param userId 用户ID
     * @return 如果用户已删除返回true，否则返回false
     */
    Boolean isUserDeleted(Long userId);

    /**
     * 检查用户是否已启用
     *
     * @param userId 用户ID
     * @return 如果用户已启用返回true，否则返回false
     */
    Boolean isUserEnabled(Long userId);

    /**
     * 判断用户是否不可达
     *
     * @param userId 用户ID
     * @return 如果用户不可达则返回true，否则返回false
     */
    Boolean isUserUnaccessible(Long userId);

    /**
     * 验证用户凭证是否正确
     *
     * @param password 密码
     * @param userId   用户ID
     * @return 是否正确
     */
    Boolean verifyPassword(Long userId, String password);

    /**
     * 查询用户是否为管理员
     *
     * @param userId 用户 ID
     * @return 是否为管理员
     */
    Boolean isUserAdmin(Long userId);

    Boolean isAdminExist(Long adminId);

    /**
     * 检查权限ID是否存在
     *
     * @param authorityIds 权限ID数组
     * @return 权限是否存在
     */
    Boolean isAuthoritiesExist(Long... authorityIds);

    /**
     * 检查指定的账号或邮箱是否已被使用
     *
     * @param s 要检查的账号或邮箱
     * @return 如果已存在返回true，不存在返回false
     */
    Boolean hasAccountOrEmail(String s);

    Long loginUserOrE();
}

package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserAuth;

/**
 * 用户认证服务接口
 *
 * @author linexsong
 */
public interface UserAuthService extends IService<UserAuth> {
    /**
     * 获取当前登录用户的ID。此方法会验证用户的登录状态和认证信息。
     *
     * @return 当前登录用户的ID
     * @throws Exception 当用户未登录、认证失败或系统异常时抛出异常
     */
    Long loginUserOrE() throws Exception;

    /**
     * 创建用户认证信息
     *
     * @param userAuth 用户认证信息对象
     * @return 如果创建成功返回true，否则返回false
     */
    Boolean createAuth(UserAuth userAuth);

    /**
     * 检查用户认证信息是否存在
     *
     * @param userId 用户ID
     * @return 如果认证信息存在返回true，否则返回false
     */
    Boolean isAuthExist(Long userId);

    /**
     * 使用密码创建用户认证信息
     *
     * @param password 用户密码
     * @return 创建的用户认证信息对象
     */
    UserAuth createAuth(String password);

    /**
     * 启用用户认证状态
     *
     * @param userId 用户ID
     */
    void enableAuth(Long userId);

    /**
     * 禁用用户认证状态
     *
     * @param userId 用户ID
     */
    void disableAuth(Long userId);

    /**
     * 删除用户认证信息
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    Boolean deleteAuth(Long userId);

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
    Boolean verifyPassword(String password, Long userId);
}

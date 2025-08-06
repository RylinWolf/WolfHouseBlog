package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserAuth;

/**
 * @author linexsong
 */
public interface UserAuthService extends IService<UserAuth> {
    /**
     * 创建用户认证信息
     *
     * @param userAuth 用户认证信息对象
     * @return 创建是否成功
     */
    Boolean createAuth(UserAuth userAuth);

    Boolean isAuthExist(Long userId);

    /**
     * 创建用户认证信息
     *
     * @param password 用户密码
     * @return 创建的用户认证信息对象
     */
    UserAuth createAuth(String password);

    /**
     * 启用用户
     *
     * @param userId 用户 ID
     */
    void enableAuth(Long userId);

    void disableAuth(Long userId);

    /**
     * 删除用户认证信息
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    Boolean deleteAuth(Long userId);

    Boolean isUserDeleted(Long userId);

    Boolean isUserEnabled(Long userId);

    Boolean isUserUnaccessable(Long userId);

    /**
     * 验证用户凭证是否正确
     *
     * @param password 密码
     * @param userId   用户 ID
     * @return 是否正确
     */
    Boolean verifyPassword(String password, Long userId);
}

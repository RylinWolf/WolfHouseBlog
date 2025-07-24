package com.wolfhouse.wolfhouseblog.services;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;

import java.util.Optional;

/**
 * @author linexsong
 */
public interface UserService extends IService<User> {
    /**
     * 根据账号或邮箱查询
     *
     * @param s 账号或邮箱
     * @return 用户 Optional 对象
     */
    Optional<User> findByAccountOrEmail(String s);
}

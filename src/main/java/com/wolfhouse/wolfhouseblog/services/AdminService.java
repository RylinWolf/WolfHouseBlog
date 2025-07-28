package com.wolfhouse.wolfhouseblog.services;

import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;

import java.util.List;
import java.util.Optional;

/**
 * @author linexsong
 */
public interface AdminService {
    /**
     * 查询用户是否为管理员
     *
     * @param userId 用户 ID
     * @return 是否为管理员
     */
    Boolean isUserAdmin(Long userId);

    /**
     * 通过管理员 ID 获取管理员信息
     *
     * @param id 管理员 ID
     * @return 管理员信息
     */
    Optional<Admin> getAdminById(Long id);

    /**
     * 通过用户 ID 获取管理员信息
     *
     * @param userId 用户 ID
     * @return 管理员信息
     */
    Optional<Admin> getAdminByUserId(Long userId);

    /**
     * 获取权限列表
     *
     * @param userId 用户 ID
     * @return 权限列表
     */
    List<Authority> getAuthorities(Long userId);
}

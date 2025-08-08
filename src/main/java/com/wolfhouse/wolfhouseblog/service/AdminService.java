package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.AdminVo;

import java.util.List;
import java.util.Optional;

/**
 * 管理员服务接口 - 处理管理员相关的业务逻辑
 *
 * @author linexsong
 */
public interface AdminService extends IService<Admin> {
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
     * 通过管理员ID获取管理员视图对象
     *
     * @param id 管理员ID
     * @return 管理员视图对象
     */
    AdminVo getAdminVoById(Long id);

    /**
     * 获取权限列表
     *
     * @param userId 用户 ID
     * @return 权限列表
     */
    List<Authority> getAuthorities(Long userId);

    /**
     * 创建新的管理员
     *
     * @param dto 管理员创建数据传输对象
     * @return 管理员视图对象
     * @throws Exception 创建过程中可能发生的异常
     */
    AdminVo createAdmin(AdminPostDto dto) throws Exception;


    /**
     * 更新管理员信息
     *
     * @param dto 管理员更新数据传输对象
     * @return 更新后的管理员视图对象
     * @throws Exception 更新过程中可能发生的异常
     */
    AdminVo updateAdmin(AdminUpdateDto dto) throws Exception;

    Boolean isAuthoritiesExist(Long... authorityIds);

    Long[] getAuthoritiesByAdmin(Long adminId);
}

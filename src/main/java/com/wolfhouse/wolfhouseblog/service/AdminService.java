package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.AdminVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.AuthorityVo;

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
     * 通过管理员ID获取管理员视图对象，包含管理员的基本信息和关联数据
     *
     * @param id 管理员ID
     * @return 管理员视图对象，包含详细信息
     * @throws Exception 获取过程中可能发生的异常
     */
    AdminVo getAdminVoById(Long id) throws Exception;

    /**
     * 获取用户的权限ID列表
     *
     * @param userId 用户ID
     * @return 权限ID列表
     * @throws Exception 获取过程中可能发生的异常
     */
    List<Authority> getAuthorities(Long userId) throws Exception;

    /**
     * 获取用户的权限ID列表
     *
     * @param userId 用户ID
     * @return 权限ID列表
     * @throws Exception 获取过程中可能发生的异常
     */
    List<Long> getAuthoritiesIds(Long userId) throws Exception;

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

    /**
     * 检查权限ID是否存在
     *
     * @param authorityIds 权限ID数组
     * @return 权限是否存在
     */
    Boolean isAuthoritiesExist(Long... authorityIds);

    /**
     * 获取指定管理员的权限ID列表
     *
     * @param adminId 管理员ID
     * @return 权限ID列表
     * @throws Exception 获取过程中可能发生的异常
     */
    List<Long> getAuthoritiesIdsByAdmin(Long adminId) throws Exception;

    /**
     * 获取指定管理员的权限视图对象列表
     *
     * @param adminId 管理员ID
     * @return 权限视图对象列表
     * @throws Exception 获取过程中可能发生的异常
     */
    List<AuthorityVo> getAuthoritiesByAdminId(Long adminId) throws Exception;

    /**
     * 更新管理员的权限列表
     *
     * @param adminId     管理员ID
     * @param authorities 新的权限ID数组
     * @return 更新影响的记录数
     * @throws Exception 更新过程中可能发生的异常
     */
    Integer changeAuthorities(Long adminId, Long[] authorities) throws Exception;

    /**
     * 删除指定管理员
     *
     * @param adminId 要删除的管理员ID
     * @return 删除是否成功
     * @throws Exception 删除过程中可能发生的异常
     */
    Boolean delete(Long adminId) throws Exception;
}

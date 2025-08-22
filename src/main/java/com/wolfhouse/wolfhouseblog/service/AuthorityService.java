package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.AuthorityByIdDto;

import java.util.List;
import java.util.Set;

/**
 * @author linexsong
 */
public interface AuthorityService extends IService<Authority> {

    /**
     * 为管理员添加权限
     *
     * @param dto 包含管理员ID和权限ID的数据传输对象
     * @return 添加是否成功
     */
    Boolean addAuthorityByIds(AuthorityByIdDto dto);

    /**
     * 删除管理员的指定权限
     *
     * @param dto 包含管理员ID和要删除的权限ID的数据传输对象
     * @return 删除是否成功
     */
    Boolean deleteAuthorityByIds(AuthorityByIdDto dto);

    /**
     * 更新管理员的权限信息
     *
     * @param dto 包含更新信息的数据传输对象
     * @return 更新后的权限列表
     * @throws Exception 更新过程中可能发生的异常
     */
    List<Authority> updateAuthority(AuthorityByIdDto dto) throws Exception;

    /**
     * 删除指定管理员的所有权限
     *
     * @param adminId 管理员ID
     * @return 删除的权限数量
     */
    Integer deleteAllAuthorities(Long adminId);

    /**
     * 更新管理员的权限列表
     *
     * @param dto 包含管理员ID和权限ID列表的数据传输对象
     * @return 更新影响的记录数
     * @throws Exception 更新过程中可能发生的异常
     */
    Integer changeAuthorities(AuthorityByIdDto dto) throws Exception;

    /**
     * 获取指定管理员的所有权限ID
     *
     * @param adminId 管理员ID
     * @return 权限ID集合
     */
    Set<Long> getAuthoritiesIds(Long adminId);
}

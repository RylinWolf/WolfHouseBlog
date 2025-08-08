package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author linexsong
 */
@Mapper
public interface AuthorityMapper extends BaseMapper<Authority> {
    /**
     * 为指定管理员添加权限。
     *
     * @param adminId     管理员的 ID
     * @param authorities 权限 ID 的列表
     * @return 受影响的行数
     */
    Integer addAuthorities(Long adminId, Long... authorities);

    /**
     * 根据管理员 ID 查询其关联的权限 ID 列表。
     *
     * @param adminId 管理员的 ID
     * @return 关联的权限 ID 的数组，如果未找到则返回空数组
     */
    @Select("select authority_id from admin_authority where admin_id = #{adminId}")
    Long[] getIdsByAdminId(Long adminId);
}

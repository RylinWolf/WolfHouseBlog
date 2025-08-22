package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.AdminAuth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 管理员权限映射接口
 *
 * @author linexsong
 */
@Mapper
public interface AdminAuthMapper extends BaseMapper<AdminAuth> {
    /**
     * 为管理员添加多个权限
     *
     * @param adminId 管理员ID
     * @param ids     权限ID集合
     * @return 添加成功的权限数量
     */
    Integer addByIds(Long adminId, Set<Long> ids);

    /**
     * 删除管理员的所有权限
     *
     * @param adminId 管理员ID
     * @return 删除的权限数量
     */
    @Delete("delete from admin_authority where admin_id = #{adminId}")
    Integer deleteAllByAdmin(Long adminId);

    /**
     * 获取管理员的所有权限ID
     *
     * @param adminId 管理员ID
     * @return 权限ID列表
     */
    @Select("select authority_id from admin_authority where admin_id = #{adminId}")
    List<Long> getIdsByAdminId(Long adminId);
}

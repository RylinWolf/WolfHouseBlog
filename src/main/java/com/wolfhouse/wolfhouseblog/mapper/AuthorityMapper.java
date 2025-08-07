package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import org.apache.ibatis.annotations.Mapper;

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
}

package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.AdminAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * @author linexsong
 */
@Mapper
public interface AdminAuthMapper extends BaseMapper<AdminAuth> {
    Integer addByIds(Long adminId, Set<Long> ids);
}

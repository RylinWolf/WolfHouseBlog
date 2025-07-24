package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * @author linexsong
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    /**
     * 通过用户 ID 获取管理员
     *
     * @param userId 用户ID
     * @return 管理员对象
     */
    Optional<Admin> getAdminByUserId(Long userId);

    /**
     * 通过管理员 ID 获取管理员
     *
     * @param id 管理员ID
     * @return 管理员对象
     */
    Optional<Admin> getAdminById(Long id);
}

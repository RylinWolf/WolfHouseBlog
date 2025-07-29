package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linexsong
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserAuth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * 用户认证数据访问接口
 *
 * @author linexsong
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuth> {

    /**
     * 插入一条用户认证记录
     *
     * @param auth 用户认证信息对象
     * @return 影响的行数
     */
    @Insert("insert into user_auth(password) values (#{password})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    Integer insertOne(UserAuth auth);
}

package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserTag;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;
import java.util.Set;

/**
 * @author linexsong
 */
@Mapper
public interface UserTagMapper extends BaseMapper<UserTag> {
    @MapKey(value = "id")
    Map<Long, Long> getTagUsingCount(Set<Long> ids);
}

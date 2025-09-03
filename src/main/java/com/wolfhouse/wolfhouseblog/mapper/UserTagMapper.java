package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserTag;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;
import java.util.Set;

/**
 * 用户标签数据访问层接口
 *
 * @author linexsong
 */
@Mapper
public interface UserTagMapper extends BaseMapper<UserTag> {

    /**
     * 获取指定标签ID集合中每个标签的使用次数
     *
     * @param ids 标签ID集合
     * @return 标签ID到使用次数的映射
     */
    @MapKey(value = "id")
    Map<Long, Long> getTagUsingCount(Set<Long> ids);
}

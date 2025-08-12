package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * @author linexsong
 */
@Mapper
public interface PartitionMapper extends BaseMapper<Partition> {

    /**
     * 获取指定分区 ID 的孩子 ID
     *
     * @param partitionId
     * @return
     */
    @Select("select id from `partition` where parent_id = #{partitionId}")
    Set<Long> getChildrenIds(Long partitionId);
}

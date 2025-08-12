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
     * 获取指定分区 ID 的孩子 ID。该方法将查询数据库中指定父分区下的所有直接子分区ID，
     * 并按照order字段进行排序。
     *
     * @param partitionId 父分区的ID，用于查找其下的所有直接子分区
     * @return 返回包含所有子分区ID的Set集合，按order字段排序。如果没有子分区则返回空Set
     */
    @Select("select id from `partition` where parent_id = #{partitionId} order by `order`, id")
    Set<Long> getChildrenIds(Long partitionId);
}

package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签数据访问层接口
 * 提供标签相关的数据库操作方法
 *
 * @author linexsong
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据用户ID获取该用户关联的所有标签
     *
     * @param userId 用户ID
     * @return 返回与该用户关联的标签列表
     */
    @Select("select * from tag where id in (select tag_id `id` from user_tag where user_id = #{userId})")
    List<Tag> getTagsByUserId(Long userId);


    /**
     * 根据用户ID获取该用户关联的所有标签ID
     *
     * @param userId 用户ID
     * @return 返回与该用户关联的标签ID列表
     */
    @Select("select tag_id from user_tag where user_id = #{userId}")
    List<Long> getTagIdsByUserId(Long userId);

    /**
     * 根据标签名称获取标签ID
     *
     * @param name 标签名称
     * @return 返回对应的标签ID，如果不存在则返回null
     */
    @Select("select id from tag where `name` = #{name}")
    Long getTagIdByName(String name);
}

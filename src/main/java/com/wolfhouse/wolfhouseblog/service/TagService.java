package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Tag;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagDeleteDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.TagVo;

import java.util.List;
import java.util.Set;

/**
 * 标签服务接口，提供标签相关的业务操作
 *
 * @author linexsong
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取所有标签的视图对象列表
     *
     * @return 标签视图对象列表
     * @throws Exception 处理异常时抛出
     */
    List<TagVo> getTagVos() throws Exception;

    /**
     * 获取所有标签实体列表
     *
     * @return 标签实体列表
     * @throws Exception 处理异常时抛出
     */
    List<Tag> getTags() throws Exception;

    /**
     * 根据ID获取标签视图对象
     *
     * @param id 标签ID
     * @return 标签视图对象
     * @throws Exception 处理异常时抛出
     */
    TagVo getTagVoById(Long id) throws Exception;

    /**
     * 检查用户的多个标签是否存在
     *
     * @param userId 用户ID
     * @param tagId  标签ID集合
     * @return 是否存在
     */
    Boolean isUserTagsExist(Long userId, Set<Long> tagId);

    /**
     * 检查用户的单个标签是否存在
     *
     * @param userId 用户ID
     * @param tagId  标签ID
     * @return 是否存在
     */
    Boolean isUserTagExist(Long userId, Long tagId);

    /**
     * 添加新标签
     *
     * @param dto 标签数据传输对象
     * @return 更新后的标签视图对象列表
     * @throws Exception 处理异常时抛出
     */
    List<TagVo> addTag(TagDto dto) throws Exception;

    /**
     * 删除标签
     *
     * @param dto 标签删除数据传输对象
     * @return 删除操作是否成功
     * @throws Exception 处理异常时抛出
     */
    Boolean deleteTags(TagDeleteDto dto) throws Exception;

    /**
     * 更新标签信息
     *
     * @param dto 标签更新数据传输对象
     * @return 更新后的标签视图对象
     * @throws Exception 处理异常时抛出
     */
    TagVo updateTag(TagUpdateDto dto) throws Exception;
}

package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Tag;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.TagVo;

import java.util.List;

/**
 * @author linexsong
 */
public interface TagService extends IService<Tag> {
    List<TagVo> getTagVos() throws Exception;

    List<Tag> getTags() throws Exception;

    TagVo getTagById(Long id) throws Exception;

    Boolean isUserTagsExist(Long userId, Set<Long> tagId);

    Boolean isUserTagExist(Long userId, Long tagId);

    List<TagVo> addTag(TagDto dto) throws Exception;
}

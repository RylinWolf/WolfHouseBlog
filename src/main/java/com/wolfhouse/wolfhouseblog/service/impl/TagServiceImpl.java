package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.tag.TagVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.TagConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.mapper.TagMapper;
import com.wolfhouse.wolfhouseblog.mapper.UserTagMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Tag;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserTag;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.TagVo;
import com.wolfhouse.wolfhouseblog.service.TagService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    private final UserAuthService authService;
    private final UserTagMapper userTagMapper;

    @Override
    public List<TagVo> getTagVos() throws Exception {
        return BeanUtil.copyList(getTags(), TagVo.class);
    }

    @Override
    public List<Tag> getTags() throws Exception {
        Long login = authService.loginUserOrE();
        return mapper.getTagsByUserId(login);
    }

    @Override
    public TagVo getTagById(Long id) throws Exception {
        Long login = authService.loginUserOrE();
        // 验证是否可达
        VerifyTool.of(TagVerifyNode.id(this)
                                   .target(id)
                                   .userId(login))
                  .doVerify();
        return BeanUtil.copyProperties(mapper.selectOneById(id), TagVo.class);
    }

    @Override
    public Boolean isUserTagExist(Long userId, Long tagId) {
        return userTagMapper.selectCountByQuery(QueryWrapper.create()
                                                            .eq(UserTag::getUserId, userId)
                                                            .eq(UserTag::getTagId, tagId)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TagVo> addTag(TagDto dto) throws Exception {
        Long login = authService.loginUserOrE();
        String name = dto.getName();

        // 验证标签名称格式
        VerifyTool.of(TagVerifyNode.NAME.target(name))
                  .doVerify();

        Long tagId = mapper.getTagIdByName(name);

        // 用户已有该标签
        if (tagId != null && isUserTagExist(login, tagId)) {
            throw new ServiceException(TagConstant.ALREADY_EXIST);
        }

        Tag tag = BeanUtil.copyProperties(dto, Tag.class);
        if (tagId == null) {
            // 标签首次添加
            mapper.insert(tag);
            tagId = tag.getId();
        } else {
            // 重复使用该 ID
            tag.setId(tagId);
        }

        // 添加 ID
        if (userTagMapper.insert(new UserTag(login, tagId)) != 1) {
            throw new ServiceException(TagConstant.ADD_FAILED);
        }
        return getTagVos();
    }
}

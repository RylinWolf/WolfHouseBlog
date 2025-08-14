package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.tag.TagVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.mapper.TagMapper;
import com.wolfhouse.wolfhouseblog.mapper.UserTagMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Tag;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserTag;
import com.wolfhouse.wolfhouseblog.pojo.vo.TagVo;
import com.wolfhouse.wolfhouseblog.service.TagService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}

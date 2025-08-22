package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.services.TagConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.tag.TagVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.TagMapper;
import com.wolfhouse.wolfhouseblog.mapper.UserTagMapper;
import com.wolfhouse.wolfhouseblog.mq.service.MqArticleService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Tag;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserTag;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagDeleteDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.TagUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqArticleTagRemoveDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.TagVo;
import com.wolfhouse.wolfhouseblog.service.TagService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.UserTagTableDef.USER_TAG;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    private final UserAuthService authService;
    private final UserTagMapper userTagMapper;
    private final MqArticleService mqArticleService;

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
    public TagVo getTagVoById(Long id) throws Exception {
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
        return isUserTagsExist(userId, Set.of(tagId));
    }

    @Override
    public Boolean isUserTagsExist(Long userId, Set<Long> tagId) {
        return userTagMapper.selectCountByQuery(QueryWrapper.create()
                                                            .eq(UserTag::getUserId, userId)
                                                            .in(UserTag::getTagId, tagId)) == tagId.size();
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTags(TagDeleteDto dto) throws Exception {
        Long login = authService.loginUserOrE();
        Set<Long> ids = dto.getIds();

        // 用户常用标签不存在
        Boolean exist = isUserTagsExist(login, ids);
        if (!exist) {
            throw new ServiceException(TagConstant.NOT_EXIST);
        }

        // 移除用户常用标签
        userTagMapper.deleteByQuery(QueryWrapper.create()
                                                .where(USER_TAG.USER_ID.eq(login)
                                                                       .and(USER_TAG.TAG_ID.in(ids))));
        // 获取常用标签仍在使用的用户数量
        Map<Long, Long> usingCount = userTagMapper.getTagUsingCount(ids);
        Set<Long> tagInUse = usingCount.keySet();
        // 有标签变为野标签，移除
        if (ids.size() != tagInUse.size()) {
            // 去除仍在使用的标签，剩下的是野标签
            ids.removeAll(tagInUse);
            mapper.deleteBatchByIds(ids);
        }

        // TODO 未解决事务问题
        // 通知文章服务移除标签
        mqArticleService.articleComUseTagsRemove(new MqArticleTagRemoveDto(login, ids));
        return true;
    }

    @Override
    public TagVo updateTag(TagUpdateDto dto) throws Exception {
        Long login = authService.loginUserOrE();
        Long id = dto.getId();

        VerifyTool.of(
                       TagVerifyNode.id(this)
                                    .userId(login)
                                    .target(id),
                       TagVerifyNode.NAME.target(dto.getName()))
                  .doVerify();

        Long nameTagId = mapper.getTagIdByName(dto.getName());
        if (nameTagId != null && !nameTagId.equals(id)) {
            throw new ServiceException(TagConstant.ALREADY_EXIST);
        }

        Tag tag = BeanUtil.copyProperties(dto, Tag.class);
        if (mapper.update(tag) != 1) {
            throw new ServiceException(TagConstant.UPDATE_FAILED);
        }
        return getTagVoById(id);
    }
}

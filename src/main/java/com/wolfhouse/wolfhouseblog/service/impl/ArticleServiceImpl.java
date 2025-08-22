package com.wolfhouse.wolfhouseblog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JsonNullableUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyStrategy;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyChain;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.ArticleVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.ComUseTagVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.IdReachableVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition.PartitionVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.ArticleMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource(name = "jsonNullableObjectMapper")
    private ObjectMapper jsonNullableObjectMapper;
    private final PartitionService partitionService;
    private final UserAuthService authService;
    /** 常用标签验证节点 */
    private final ComUseTagVerifyNode comUseTagVerifyNode;


    @Override
    public Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        // TODO 通过 Nullable 实现可查空
        var userId = ServiceUtil.loginUser();

        var wrapper = QueryWrapper.create();
        // 构建查询列
        wrapper.select(columns);
        // 查询当前用户的私人日记和全部公开日记
        wrapper.and(q -> {
            q.eq(Article::getVisibility, VisibilityEnum.PUBLIC);
            if (userId != null) {
                q.or(q2 -> {
                    q2.eq(Article::getVisibility, VisibilityEnum.PRIVATE)
                      .eq(Article::getAuthorId, userId);
                });
            }
        });
        // 构建查询条件
        wrapper.eq(
                    Article::getId,
                    dto.getId()
                       .orElse(null))
               // 按标题查询
               .like(
                    Article::getTitle,
                    dto.getTitle()
                       .orElse(null))
               // 按作者查询
               .eq(
                    Article::getAuthorId,
                    dto.getAuthorId()
                       .orElse(null));
        // 分区条件
        Long partitionId = dto.getPartitionId()
                              .orElse(null);
        // 分区可达
        if (partitionService.isUserPartitionReachable(userId, partitionId)) {
            wrapper.eq(Article::getPartitionId, partitionId);
        }

        // 日期范围查询
        LocalDateTime start = dto.getPostStart()
                                 .orElse(null);
        LocalDateTime end = dto.getPostEnd()
                               .orElse(null);

        if (BeanUtil.isAnyNotBlank(start, end)) {
            wrapper.ge(Article::getPostTime, start, start != null);
            wrapper.le(Article::getPostTime, end, end != null);
        }

        return mapper.paginate(dto.getPageNumber(), dto.getPageSize(), wrapper);
    }

    @Override
    public PageResult<ArticleVo> getQuery(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        return PageResult.of(queryBy(dto, columns), ArticleVo.class);
    }

    @Override
    public PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) throws Exception {
        return PageResult.of(queryBy(dto, ArticleConstant.BRIEF_COLUMNS), ArticleBriefVo.class);
    }

    @Override
    public ArticleVo getVoById(Long id) throws Exception {
        BaseVerifyChain chain = VerifyTool.of(new IdReachableVerifyNode(this).target(id)
                                                                             .setStrategy(VerifyStrategy.NORMAL));

        return chain.doVerify() ? BeanUtil.copyProperties(mapper.selectOneById(id), ArticleVo.class) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVo post(ArticleDto dto) throws Exception {
        Long login = authService.loginUserOrE();

        // TODO 常用标签验证
        VerifyTool.of(
                       ArticleVerifyNode.TITLE.target(dto.getTitle())
                                              .exception(ARTICLE.TITLE.getName()),
                       ArticleVerifyNode.CONTENT.target(dto.getContent())
                                                .exception(ARTICLE.CONTENT.getName()),
                       ArticleVerifyNode.PRIMARY.target(dto.getPrimary())
                                                .allowNull(true)
                                                .exception(ARTICLE.PRIMARY.getName()),
                       comUseTagVerifyNode.userId(login)
                                          .target(dto.getComUseTags())
                                          .allowNull(true))
                  .doVerify();

        Article article = BeanUtil.copyProperties(dto, Article.class);
        article.setAuthorId(login);

        mapper.insertWithPkBack(article);
        return getVoById(article.getId());
    }

    @Override
    public ArticleVo update(ArticleUpdateDto dto) throws Exception {
        // TODO 常用标签验证
        String title = JsonNullableUtil.getObjOrNull(dto.getTitle());
        String content = JsonNullableUtil.getObjOrNull(dto.getContent());
        String primary = JsonNullableUtil.getObjOrNull(dto.getPrimary());
        Long partitionId = JsonNullableUtil.getObjOrNull(dto.getPartitionId());
        Set<Long> comUseTags = JsonNullableUtil.getObjOrNull(dto.getComUseTags());

        VerifyTool.ofLoginExist(
                       authService,
                       // 文章 ID
                       ArticleVerifyNode.id(this)
                                        .target(dto.getId()),
                       // 标题
                       ArticleVerifyNode.title(title, true)
                                        .exception(ARTICLE.TITLE.getName()),
                       // 内容
                       ArticleVerifyNode.content(content, true)
                                        .exception(ARTICLE.CONTENT.getName()),
                       // 摘要
                       ArticleVerifyNode.primary(primary, true)
                                        .exception(ARTICLE.PRIMARY.getName()),
                       // 分区 ID
                       PartitionVerifyNode.id(partitionService)
                                          .target(partitionId)
                                          .allowNull(true),
                       // 常用标签
                       comUseTagVerifyNode.target(comUseTags)
                                          .allowNull(true),

                       // 不得全为空
                       new NotAllBlankVerifyNode(
                            title,
                            content,
                            primary,
                            comUseTags,
                            dto.getVisibility(),
                            dto.getPartitionId(),
                            dto.getTags())
                            .exception(new ServiceException(VerifyConstant.NOT_ALL_BLANK)))
                  .doVerify();

        // 设置标题、内容
        UpdateChain<Article> updateChain = UpdateChain.of(Article.class)
                                                      .where(ARTICLE.ID.eq(dto.getId()))
                                                      .set(ARTICLE.TITLE, title, title != null)
                                                      .set(ARTICLE.CONTENT, content, content != null);
        // 可见性
        dto.getVisibility()
           .ifPresent(v -> updateChain.set(ARTICLE.VISIBILITY, v));
        // 标签
        dto.getTags()
           .ifPresent(t -> updateChain.set(ARTICLE.TAGS, t));
        // 常用标签
        dto.getComUseTags()
           .ifPresent(t -> updateChain.set(ARTICLE.COM_USE_TAGS, t));
        // 摘要
        dto.getPrimary()
           .ifPresent(p -> updateChain.set(ARTICLE.PRIMARY, p));
        // 分区
        dto.getPartitionId()
           .ifPresent(p -> updateChain.set(ARTICLE.PARTITION_ID, p));

        if (!updateChain.update()) {
            throw new ServiceException(ArticleConstant.UPDATE_FAILED);
        }

        return getVoById(dto.getId());
    }

    @Override
    public Boolean deleteById(Long id) throws Exception {
        VerifyTool.ofLogin(ArticleVerifyNode.id(this)
                                            .target(id))
                  .doVerify();

        return mapper.deleteById(id) == 1;
    }
}

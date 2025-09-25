package com.wolfhouse.wolfhouseblog.service.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
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
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.IdOwnVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.IdReachableVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.NotAnyBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition.PartitionVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.ArticleDraftMapper;
import com.wolfhouse.wolfhouseblog.mapper.ArticleMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.domain.ArticleDraft;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDraftDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleDraftTableDef.ARTICLE_DRAFT;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;

/**
 * @author linexsong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    private final ArticleDraftMapper draftMapper;

    private final PartitionService partitionService;
    private final ServiceAuthMediator mediator;

    /**
     * 常用标签验证节点
     */
    private final ComUseTagVerifyNode comUseTagVerifyNode;
    @Resource(name = "jsonNullableObjectMapper")
    private final ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        this.mediator.registerArticle(this);
    }


    @Override
    public Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        // TODO 通过 Nullable 实现可查空
        var userId = ServiceUtil.loginUser();
        var wrapper = QueryWrapper.create();
        // 构建查询列
        wrapper.select(columns);
        // 查询当前用户的私人日记和全部公开日记
        wrapperVisibilityBuild(wrapper, userId);
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
        if (!BeanUtil.isBlank(partitionId) && partitionService.isUserPartitionReachable(userId, partitionId)) {
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

        // 排序
        wrapper.orderBy(ARTICLE.POST_TIME.desc());
        return mapper.paginate(dto.getPageNumber(), dto.getPageSize(), wrapper);
    }

    /**
     * 根据用户 ID 和可见性构建查询条件的包装器。
     * 对于公开可见的内容始终添加查询条件，对于私人内容则根据用户 ID 进一步判断。
     *
     * @param wrapper 查询包装器对象，用于构建动态查询条件
     * @param userId  用户ID，用于判断是否可以查看私有内容。若为null，则忽略与用户相关的私有内容条件
     */
    private static void wrapperVisibilityBuild(QueryWrapper wrapper, Long userId) {
        wrapper.and(q -> {
            q.eq(Article::getVisibility, VisibilityEnum.PUBLIC);
            if (userId != null) {
                q.or(q2 -> {
                    q2.eq(Article::getVisibility, VisibilityEnum.PRIVATE)
                      .eq(Article::getAuthorId, userId);
                });
            }
        });
    }

    @Override
    public PageResult<ArticleVo> getQueryVo(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        return PageResult.of(queryBy(dto, columns), ArticleVo.class);
    }

    @Override
    public PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) throws Exception {

        return PageResult.of(queryBy(dto, ArticleConstant.BRIEF_COLUMNS), ArticleBriefVo.class);
    }

    @Override
    public List<ArticleBriefVo> getBriefByIds(Collection<Long> articleIds) {
        // 根据登录用户构建查询条件
        Long login = ServiceUtil.loginUser();

        if (BeanUtil.isBlank(articleIds)) {
            return null;
        }
        // 查询指定 ID 集合的文章简要信息
        var wrapper = QueryWrapper.create();
        wrapperVisibilityBuild(wrapper, login);
        wrapper.in(Article::getId, articleIds);

        return mapper.selectListByQueryAs(wrapper, ArticleBriefVo.class);
    }

    @Override
    public ArticleVo getVoById(Long id) throws Exception {
        BaseVerifyChain chain = VerifyTool.of(new IdReachableVerifyNode(mediator).target(id)
                                                                                 .setStrategy(VerifyStrategy.NORMAL));

        return chain.doVerify() ? BeanUtil.copyProperties(mapper.selectOneById(id), ArticleVo.class) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article post(ArticleDto dto) throws Exception {
        Long login = mediator.loginUserOrE();

        VerifyTool.of(
                      // 标题、内容、摘要校验
                      ArticleVerifyNode.TITLE.target(dto.getTitle())
                                             .exception(ARTICLE.TITLE.getName()),
                      ArticleVerifyNode.CONTENT.target(dto.getContent())
                                               .exception(ARTICLE.CONTENT.getName()),
                      ArticleVerifyNode.PRIMARY.target(dto.getPrimary())
                                               .allowNull(true)
                                               .exception(ARTICLE.PRIMARY.getName()),
                      // 分区 ID 校验
                      PartitionVerifyNode.id(mediator)
                                         .target(dto.getPartitionId())
                                         .allowNull(true),
                      comUseTagVerifyNode.userId(login)
                                         .target(dto.getComUseTags())
                                         .allowNull(true))
                  .doVerify();

        Article article = BeanUtil.copyProperties(dto, Article.class);
        article.setAuthorId(login);

        mapper.insertWithPkBack(article);
        // 取消暂存
        unDraft();

        return getById(article.getId());
    }

    @Override
    public ArticleVo getDraft() throws Exception {
        Long login = mediator.loginUserOrE();

        // 暂存文章
        ArticleDraft articleDraft = draftMapper.selectOneByQuery(
            QueryWrapper.create()
                        .where(ARTICLE_DRAFT
                                   .AUTHOR_ID
                                   .eq(login)));
        if (BeanUtil.isBlank(articleDraft)) {
            return null;
        }
        Long articleId = articleDraft.getArticleId();
        return getVoById(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVo draft(ArticleDraftDto article) throws Exception {
        // 0. 是否已有暂存
        ArticleVo draft = getDraft();

        // 如果已暂存且要暂存的文章 ID 匹配，则更新 并结束
        if (!BeanUtil.isBlank(draft)) {
            Long draftId = draft.getId();
            Long id = article.getId();
            // 已暂存的文章 ID 与要暂存的文章 ID 不匹配
            if (!Objects.equals(draftId, id)) {
                throw new ServiceException(ArticleConstant.DRAFT_EXIST);
            }
            // 冗余验证，登录用户为文章作者，则更新
            if (!new IdOwnVerifyNode(mediator).target(article.getId())
                                              .verify()) {
                return BeanUtil.copyProperties(update(objectMapper.convertValue(article, ArticleUpdateDto.class)),
                                               ArticleVo.class);
            }
            // 非作者
            log.error("暂存文章 ID 匹配，但作者验证未通过。已有暂存：{}, 更新暂存： {}", draft, article);
            throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
        }

        // 1. 发布文章，设置可见性为 私密
        article.setVisibility(VisibilityEnum.PRIVATE);
        Article vo = post(BeanUtil.copyProperties(article, ArticleDto.class));

        // 2. 将文章 ID 存储到文章暂存中
        int i = draftMapper.insert(new ArticleDraft(null, vo.getAuthorId(), vo.getId()));
        if (i != 1) {
            throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
        }
        return BeanUtil.copyProperties(vo, ArticleVo.class);
    }

    @Override
    public Boolean unDraft() throws Exception {
        ArticleVo draft = getDraft();
        if (BeanUtil.isBlank(draft)) {
            return false;
        }

        Long articleId = draft.getId();
        return draftMapper.deleteByQuery(QueryWrapper.create()
                                                     .where(ARTICLE_DRAFT.ARTICLE_ID.eq(articleId))) > 0;
    }

    @Override
    public Article update(ArticleUpdateDto dto) throws Exception {
        String title = JsonNullableUtil.getObjOrNull(dto.getTitle());
        String content = JsonNullableUtil.getObjOrNull(dto.getContent());
        String primary = JsonNullableUtil.getObjOrNull(dto.getPrimary());
        Long partitionId = JsonNullableUtil.getObjOrNull(dto.getPartitionId());
        Set<Long> comUseTags = JsonNullableUtil.getObjOrNull(dto.getComUseTags());

        VerifyTool.ofLoginExist(
                      mediator,
                      // 文章 ID
                      ArticleVerifyNode.idOwn(mediator)
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
                      PartitionVerifyNode.id(mediator)
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

        return getById(dto.getId());
    }

    @Override
    public Boolean deleteById(Long id) throws Exception {
        VerifyTool.ofLogin(ArticleVerifyNode.idReachable(mediator)
                                            .target(id))
                  .doVerify();
        // 取消暂存
        int i = mapper.deleteById(id);
        if (i != 1) {
            return false;
        }
        unDraft();
        return true;
    }

    @Override
    public Boolean isArticleReachable(Long userId, Long articleId) throws Exception {
        VerifyTool.of(new NotAnyBlankVerifyNode(articleId))
                  .doVerify();

        // 公开文章或当前用户的私密文章
        long count = mapper.selectCountByQuery(
            QueryWrapper.create()
                        .where(ARTICLE.ID.eq(articleId))
                        .and(ARTICLE.VISIBILITY.eq(VisibilityEnum.PUBLIC)
                                               .or(ARTICLE.AUTHOR_ID.eq(userId))));
        if (count == 0) {
            return false;
        }
        if (count == 1) {
            return true;
        }
        log.error("检查文章是否可达时出现问题：{}, {}", userId, articleId);
        throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
    }

    @Override
    public Boolean isArticleOwner(Long articleId, Long login) {
        return mapper.selectOneById(articleId)
                     .getAuthorId()
                     .equals(login);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<Long> addViews(Map<String, Long> views) {
        ConcurrentHashSet<Long> ids = new ConcurrentHashSet<>();
        views.forEach((k, v) -> {
            UpdateChain<Article> chain = UpdateChain.of(Article.class);
            chain.setRaw(ARTICLE.VIEWS, ARTICLE.VIEWS.getName() + "+" + v);
            chain.where(ARTICLE.ID.eq(Long.valueOf(k)));
            if (chain.update()) {
                ids.add(Long.valueOf(k));
            }
        });
        if (ids.size() != views.size()) {
            throw new ServiceException(ArticleConstant.UPDATE_INCOMPLETE);
        }
        return new HashSet<>(ids);
    }

    @Override
    public Boolean addViews(Long articleId, Long views) {
        UpdateChain<Article> chain = UpdateChain.of(Article.class);
        chain.setRaw(ARTICLE.VIEWS, ARTICLE.VIEWS.getName() + "+" + views);
        chain.where(ARTICLE.ID.eq(articleId));
        return chain.update();
    }
}

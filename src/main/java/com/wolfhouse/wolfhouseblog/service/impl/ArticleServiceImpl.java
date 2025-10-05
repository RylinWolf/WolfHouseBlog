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
import com.wolfhouse.wolfhouseblog.pojo.vo.UserBriefVo;
import com.wolfhouse.wolfhouseblog.service.ArticleActionService;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import com.wolfhouse.wolfhouseblog.service.mediator.ArticleEsDbMediator;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleDraftTableDef.ARTICLE_DRAFT;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.PartitionTableDef.PARTITION;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.UserTableDef.USER;

/**
 * @author linexsong
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    private final ArticleDraftMapper draftMapper;
    private final ArticleActionService actionService;
    private final PartitionService partitionService;
    private final ServiceAuthMediator mediator;
    private final ArticleEsDbMediator esDbMediator;

    /**
     * 常用标签验证节点
     */
    private final ComUseTagVerifyNode comUseTagVerifyNode;
    @Resource(name = "jsonNullableObjectMapper")
    private final ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        // 注册中介者
        this.mediator.registerArticle(this);
        this.esDbMediator.registerArticleService(this);
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
        Page<Article> articlePage = queryBy(dto, ARTICLE.ID);
        List<Long> ids = articlePage.getRecords()
                                    .stream()
                                    .map(Article::getId)
                                    .toList();
        // 无结果
        if (ids.isEmpty()) {
            return PageResult.of(new Page<>(Collections.emptyList(), dto.getPageNumber(), dto.getPageSize(), 0));
        }
        List<ArticleVo> vos = getVoByIds(ids, columns);
        return PageResult.of(new Page<>(vos, dto.getPageNumber(), dto.getPageSize(), vos.size()));
    }

    @Override
    public PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) throws Exception {
        List<Long> ids = queryBy(dto, ARTICLE.ID).getRecords()
                                                 .stream()
                                                 .map(Article::getId)
                                                 .toList();
        List<ArticleBriefVo> brief = getBriefByIds(ids);
        return PageResult.of(new Page<>(brief, dto.getPageNumber(), dto.getPageSize(), brief.size()));
    }

    @Override
    public List<ArticleBriefVo> getBriefByIds(Collection<Long> articleIds) throws Exception {
        // 根据登录用户构建查询条件
        Long login = ServiceUtil.loginUser();

        if (BeanUtil.isBlank(articleIds)) {
            return null;
        }
        // 查询指定 ID 集合的文章简要信息
        var wrapper = QueryWrapper.create();
        wrapperVisibilityBuild(wrapper, login);
        wrapper.in(Article::getId, articleIds);

        return BeanUtil.copyList(getVoByIds(articleIds), ArticleBriefVo.class);
    }

    /**
     * 根据给定的ID获取对应的ArticleVo对象。
     *
     * @param id 指定需要获取的ArticleVo的ID
     * @return 返回与指定ID对应的ArticleVo对象
     * @throws Exception 如果获取过程中发生异常
     */
    @Override
    public ArticleVo getVoById(Long id) throws Exception {
        return getVoByIds(Set.of(id)).getFirst();
    }

    /**
     * 根据指定的文章 ID 集合查询文章视图对象集合。
     * 支持动态指定查询列，并注入作者信息、分区信息等数据。
     *
     * @param ids     文章 ID 的集合
     * @param columns 指定查询的列，若未指定则默认查询所有文章列、分区信息和作者信息
     * @return 包含所有符合条件的文章视图对象的列表
     * @throws Exception 当查询过程中发生异常时抛出
     */
    public List<ArticleVo> getVoByIds(Collection<Long> ids, QueryColumn... columns) throws Exception {
        // 为 Vo 注入作者、分区名
        QueryWrapper wrapper = QueryWrapper.create()
                                           .where(ARTICLE.ID.in(ids))
                                           .leftJoin(USER)
                                           .on(USER.ID.eq(ARTICLE.AUTHOR_ID))
                                           .leftJoin(PARTITION)
                                           .on(PARTITION.ID.eq(ARTICLE.PARTITION_ID));
        // 指定检索字段
        wrapper.select(columns);
        if (columns.length == 0) {
            // 获取文章所有列、分区信息和作者信息
            wrapper.select(ARTICLE.ALL_COLUMNS,
                           PARTITION.ID.as(ArticleVo::getPartitionId),
                           PARTITION.NAME.as(ArticleVo::getPartitionName));
        }

        @SuppressWarnings("unchecked")
        List<Map<?, ?>> maps = (List<Map<?, ?>>) (List<?>) mapper.selectListByQueryAs(wrapper, Map.class);

        ArrayList<ArticleVo> articleVos = new ArrayList<>();

        for (Map<?, ?> map : maps) {
            // 提取作者 ID，根据 ID 获取用户
            Object o = map.get(ARTICLE.AUTHOR_ID.getName());
            if (BeanUtil.isBlank(o)) {
                throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
            }
            long userId = Long.parseLong(o.toString());
            long articleId = Long.parseLong(map.get(ARTICLE.ID.getName())
                                               .toString());


            // 验证文章 ID 是否可达
            BaseVerifyChain chain = VerifyTool.of(
                new IdReachableVerifyNode(mediator).target(articleId)
                                                   .setStrategy(VerifyStrategy.NORMAL));
            if (!chain.doVerify()) {
                continue;
            }

            // 将用户对象转换为简略信息，并注入给文章对象
            UserBriefVo userBriefVo = BeanUtil.copyProperties(mediator.userService()
                                                                      .getUserVoById(userId),
                                                              UserBriefVo.class);
            ArticleVo articleVo = BeanUtil.copyProperties(map, ArticleVo.class);
            articleVo.setAuthor(userBriefVo);
            // 注入点赞量
            articleVo.setLikeCount(actionService.likeCount(articleId));
            articleVos.add(articleVo);
        }

        return articleVos;
    }

    @Override
    public List<ArticleVo> getVoByIds(Collection<Long> ids) throws Exception {
        return getVoByIds(ids, new QueryColumn[]{});
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
    public ArticleVo update(ArticleUpdateDto dto) throws Exception {
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

        return getVoById(dto.getId());
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

    /**
     * 批量增加多篇文章的浏览量
     *
     * @param views Map<文章ID, 增加的浏览量>
     * @return 成功更新浏览量的文章ID集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<Long> addViews(Map<String, Long> views) {
        // 使用线程安全的HashSet存储更新成功的文章ID
        ConcurrentHashSet<Long> ids = new ConcurrentHashSet<>();
        views.forEach((k, v) -> {
            // 构建更新链并设置原始SQL更新浏览量
            UpdateChain<Article> chain = UpdateChain.of(Article.class);
            chain.setRaw(ARTICLE.VIEWS, ARTICLE.VIEWS.getName() + "+" + v);
            chain.where(ARTICLE.ID.eq(Long.valueOf(k)));
            // 更新成功则记录ID
            if (chain.update()) {
                ids.add(Long.valueOf(k));
            }
        });
        // 检查是否所有文章都更新成功
        if (ids.size() != views.size()) {
            throw new ServiceException(ArticleConstant.UPDATE_INCOMPLETE);
        }
        return new HashSet<>(ids);
    }

    /**
     * 增加单篇文章的浏览量
     *
     * @param articleId 文章ID
     * @param views     要增加的浏览量
     * @return 是否更新成功
     */
    @Override
    public Boolean addViews(Long articleId, Long views) {
        // 构建更新链并设置原始SQL更新浏览量
        UpdateChain<Article> chain = UpdateChain.of(Article.class);
        chain.setRaw(ARTICLE.VIEWS, ARTICLE.VIEWS.getName() + "+" + views);
        chain.where(ARTICLE.ID.eq(articleId));
        return chain.update();
    }
}

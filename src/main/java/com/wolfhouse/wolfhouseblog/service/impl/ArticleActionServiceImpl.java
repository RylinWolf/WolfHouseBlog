package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.EmptyVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.ArticleVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites.FavoritesVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.ArticleCommentMapper;
import com.wolfhouse.wolfhouseblog.mapper.ArticleFavoriteMapper;
import com.wolfhouse.wolfhouseblog.mapper.ArticleLikeMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.ArticleComment;
import com.wolfhouse.wolfhouseblog.pojo.domain.ArticleFavorite;
import com.wolfhouse.wolfhouseblog.pojo.domain.ArticleLike;
import com.wolfhouse.wolfhouseblog.pojo.dto.*;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;
import com.wolfhouse.wolfhouseblog.service.ArticleActionService;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleCommentTableDef.ARTICLE_COMMENT;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleFavoriteTableDef.ARTICLE_FAVORITE;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleLikeTableDef.ARTICLE_LIKE;

/**
 * @author rylinwolf
 */
@Service
@RequiredArgsConstructor
public class ArticleActionServiceImpl implements ArticleActionService {
    private final ArticleService articleService;
    private final ServiceAuthMediator mediator;
    private final ArticleCommentMapper commentMapper;
    private final ArticleFavoriteMapper favoriteMapper;
    private final ArticleLikeMapper likeMapper;

    @PostConstruct
    private void init() {
        mediator.registerAction(this);
    }

    @Override
    public Boolean isArticleCommentExist(Long articleId, Long commentId) {
        return commentMapper.selectCountByQuery(
            QueryWrapper.create()
                        .where(ARTICLE_COMMENT.ARTICLE_ID.eq(articleId))
                        .and(ARTICLE_COMMENT.ID.eq(commentId))) > 0;
    }

    @Override
    public PageResult<ArticleCommentVo> getArticleCommentVos(ArticleCommentQueryDto dto) throws Exception {
        var articleId = dto.getArticleId();
        List<VerifyNode<?>> nodes = new ArrayList<>();
        // 查询条件构建
        QueryWrapper wrapper = QueryWrapper.create()
                                           .where(ARTICLE_COMMENT.ARTICLE_ID.eq(articleId));


        // 文章是否可达
        nodes.add(ArticleVerifyNode.idReachable(mediator)
                                   .target(articleId));

        // 用户是否存在
        dto.getUserId()
           .ifPresent(id -> {
               nodes.add(UserVerifyNode.id(mediator)
                                       .target(id)
                                       .allowNull(true));
               // 若验证成功，则会进入 wrapper
               wrapper.and(ARTICLE_COMMENT.USER_ID.eq(id, id != null));
           });


        // 父评论 ID
        dto.getReplyId()
           .ifPresent(rid -> {
               nodes.add(ArticleVerifyNode.commentId(mediator)
                                          .articleId(articleId)
                                          .target(rid)
                                          .allowNull(true));
               wrapper.and(ARTICLE_COMMENT.REPLY_ID.eq(rid, rid != null));
           });

        // 执行验证
        VerifyTool.ofLoginExist(mediator, nodes.toArray(new VerifyNode<?>[0]))
                  .doVerify();
        // 分页查询
        return PageResult.of(commentMapper.paginateAs(
            dto.getPageNumber(),
            dto.getPageSize(),
            wrapper,
            ArticleCommentVo.class));
    }

    @Override
    public PageResult<ArticleCommentVo> getArticleCommentVosByArticle(Long articleId) throws Exception {
        ArticleCommentQueryDto dto = new ArticleCommentQueryDto();
        dto.setArticleId(articleId);
        return getArticleCommentVos(dto);
    }

    @Override
    public PageResult<ArticleCommentVo> postComment(ArticleCommentDto dto) throws Exception {
        Long login = mediator.loginUserOrE();
        Long articleId = dto.getArticleId();
        String content = dto.getContent();
        VerifyTool.of(
                      // 文章 ID 验证
                      ArticleVerifyNode.idReachable(mediator)
                                       .target(dto.getArticleId()),
                      // 父评论 ID 验证
                      ArticleVerifyNode.commentId(mediator)
                                       .articleId(articleId)
                                       .target(dto.getReplyId())
                                       .allowNull(true),
                      // 评论信息验证
                      new StringVerifyNode(1L, 2000L, false).target(content))
                  .doVerify();

        commentMapper.insert(ArticleComment.builder()
                                           .userId(login)
                                           .articleId(articleId)
                                           .content(content)
                                           .replyId(dto.getReplyId())
                                           .build(), true);

        return getArticleCommentVosByArticle(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult<ArticleCommentVo> deleteComment(ArticleCommentDeleteDto dto) throws Exception {
        Long commentId = dto.getCommentId();
        Long login = mediator.loginUserOrE();
        // 评论是否存在
        VerifyTool.of(ArticleVerifyNode.commentId(mediator)
                                       .target(commentId),
                      // 删除目标评论的作者是否为登录用户
                      EmptyVerifyNode.of(login)
                                     .predicate(
                                         (t) -> {
                                             ArticleComment comment = commentMapper.selectOneById(commentId);
                                             return comment.getUserId()
                                                           .equals(t);
                                         }))
                  .doVerify();

        Set<Long> ids = getReplyIds(commentId);
        int i = commentMapper.deleteBatchByIds(ids);
        if (i != ids.size()) {
            throw new ServiceException(ArticleConstant.COMMENT_DELETE_FAILED);
        }
        try {
            return getArticleCommentVosByArticle(dto.getArticleId());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private Set<Long> getReplyIds(Long commentId) {
        List<ArticleComment> comments = commentMapper.selectListByQuery(
            QueryWrapper.create()
                        .select(ARTICLE_COMMENT.ARTICLE_ID)
                        .where(ARTICLE_COMMENT.REPLY_ID.eq(commentId)));
        Set<Long> ids = comments.stream()
                                .map(ArticleComment::getId)
                                .collect(Collectors.toSet());
        Set<Long> res = new HashSet<>(ids);

        res.addAll(ids);
        ids.forEach(id -> res.addAll(getReplyIds(id)));
        return res;

    }

    @Override
    public Boolean isLiked(Long articleId) throws Exception {
        Long login = mediator.loginUserOrE();
        return likeMapper.selectCountByQuery(QueryWrapper.create()
                                                         .where(ARTICLE_LIKE.ARTICLE_ID.eq(articleId))
                                                         .and(ARTICLE_LIKE.USER_ID.eq(login))) > 0;
    }

    @Override
    public Boolean like(Long articleId) throws Exception {
        Long login = mediator.loginUserOrE();
        // 文章可达验证
        VerifyTool.of(
                      ArticleVerifyNode.idReachable(mediator)
                                       .target(articleId))
                  .doVerify();
        if (isLiked(articleId)) {
            throw new ServiceException(ArticleConstant.ALREADY_LIKED);
        }
        return likeMapper.insert(new ArticleLike(null, login, articleId, null)) == 1;
    }

    @Override
    public Boolean dislike(Long articleId) throws Exception {
        Long login = mediator.loginUserOrE();
        if (!isLiked(articleId)) {
            throw new ServiceException(ArticleConstant.NOT_LIKED);
        }
        boolean deleted = likeMapper.deleteByQuery(QueryWrapper.create()
                                                               .where(ARTICLE_LIKE.ARTICLE_ID.eq(articleId))
                                                               .and(ARTICLE_LIKE.USER_ID.eq(login))) == 1;
        if (!deleted) {
            throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
        }
        return true;
    }

    @Override
    public List<ArticleFavoriteVo> getFavoritesByArticle(Long articleId) throws Exception {
        Long login = mediator.loginUserOrE();
        return favoriteMapper
            .selectListByQueryAs(QueryWrapper.create()
                                             .where(ARTICLE_FAVORITE.USER_ID.eq(
                                                 login))
                                             .and(ARTICLE_FAVORITE.ARTICLE_ID.eq(
                                                 articleId)), ArticleFavoriteVo.class);

    }

    @Override
    public Boolean isFavoriteExist(ArticleFavoriteDto dto) {
        long count = favoriteMapper.selectCountByQuery(
            QueryWrapper.create()
                        .where(ARTICLE_FAVORITE.ARTICLE_ID.eq(dto.getArticleId()))
                        .and(ARTICLE_FAVORITE.FAVORITE_ID.eq(dto.getFavoriteId())));
        return count > 0;
    }

    @Override
    public PageResult<ArticleBriefVo> getFavoritesArticle(ArticleFavoritePageDto dto) throws Exception {
        // 指定收藏夹的收藏记录分页结果
        Page<ArticleFavorite> favoritePage = favoriteMapper.paginate(
            dto.getPageNumber(),
            dto.getPageSize(),
            QueryWrapper.create()
                        .select(ARTICLE_FAVORITE.ARTICLE_ID)
                        .where(ARTICLE_FAVORITE.FAVORITE_ID.eq(
                            dto.getFavoritesId())));
        // 根据收藏记录获取文章
        Set<Long> articleIds = favoritePage.getRecords()
                                           .stream()
                                           .map(ArticleFavorite::getArticleId)
                                           .collect(Collectors.toSet());


        // 获取结果
        List<ArticleBriefVo> brief = articleService.getBriefByIds(articleIds);
        // 收藏记录分页结果的信息即为最终的分页结果信息
        Page<ArticleBriefVo> res = new Page<>(dto.getPageNumber(), dto.getPageSize(), favoritePage.getTotalRow());
        res.setRecords(brief);
        res.setTotalPage(favoritePage.getTotalPage());

        return PageResult.of(res);
    }

    @Override
    public Boolean favorite(ArticleFavoriteDto dto) throws Exception {
        Long login = mediator.loginUserOrE();
        VerifyTool.of(
                      // 文章 ID 可达
                      ArticleVerifyNode.idReachable(mediator)
                                       .target(dto.getArticleId()),
                      // 收藏夹为本人创建
                      FavoritesVerifyNode.idOwn(mediator)
                                         .target(dto.getFavoriteId()))
                  .doVerify();
        // 已收藏
        if (isFavoriteExist(dto)) {
            throw new ServiceException(ArticleConstant.ALREADY_FAVORITE);
        }

        return favoriteMapper.insert(
            new ArticleFavorite(null,
                                dto.getFavoriteId(),
                                dto.getArticleId(),
                                login,
                                null)) > 0;
    }

    @Override
    public Boolean removeFavorite(Long articleId) {
        return null;
    }

    @Override
    public Boolean removeFavorites(Long favoritesId) {
        return null;
    }
}
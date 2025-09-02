package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.ArticleVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.ArticleCommentMapper;
import com.wolfhouse.wolfhouseblog.mapper.ArticleFavoriteMapper;
import com.wolfhouse.wolfhouseblog.mapper.ArticleLikeMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.ArticleComment;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentDeleteDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentQueryDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;
import com.wolfhouse.wolfhouseblog.service.ArticleActionService;
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

/**
 * @author rylinwolf
 */
@Service
@RequiredArgsConstructor
public class ArticleActionServiceImpl implements ArticleActionService {
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
    public PageResult<ArticleCommentVo> deleteComment(ArticleCommentDeleteDto dto) {
        Set<Long> ids = getReplyIds(dto.getCommentId());
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
    public Boolean isLiked(Long articleId) {
        return null;
    }

    @Override
    public Boolean like(Long articleId) {
        return null;
    }

    @Override
    public Boolean dislike(Long articleId) {
        return null;
    }

    @Override
    public Boolean isFavorite(Long articleId) {
        return null;
    }

    @Override
    public Boolean favorite(Long articleId) {
        return null;
    }

    @Override
    public Boolean removeFavorite(Long articleId) {
        return null;
    }
}

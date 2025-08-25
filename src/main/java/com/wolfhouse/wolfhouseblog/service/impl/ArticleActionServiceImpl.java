package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article.ArticleVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.ArticleCommentMapper;
import com.wolfhouse.wolfhouseblog.mapper.ArticleFavoriteMapper;
import com.wolfhouse.wolfhouseblog.mapper.ArticleLikeMapper;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;
import com.wolfhouse.wolfhouseblog.service.ArticleActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public Boolean isArticleCommentExist(Long articleId, Long commentId) {
        return commentMapper.selectCountByQuery(
            QueryWrapper.create()
                        .where(ARTICLE_COMMENT.ARTICLE_ID.eq(articleId))
                        .and(ARTICLE_COMMENT.ID.eq(commentId))) > 0;
    }

    @Override
    public PageResult<ArticleCommentVo> getArticleCommentVos(ArticleCommentQueryDto dto) {
        var articleId = dto.getArticleId();
        // 文章是否可达
        VerifyTool.ofLoginExist(mediator,
            ArticleVerifyNode.id(mediator)
                             .target(articleId));
        // 分页查询
        return PageResult.of(commentMapper.paginateAs(
            dto.getPageNumber(),
            dto.getPageSize(),
            QueryWrapper.create()
                        .where(ARTICLE_COMMENT.ARTICLE_ID.eq(articleId)),
            ArticleCommentVo.class));
    }

    @Override
    public PageResult<ArticleCommentVo> postComment(ArticleCommentDto dto) {
        return null;
    }

    @Override
    public PageResult<ArticleCommentVo> deleteComment(Long commentId) {
        return null;
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

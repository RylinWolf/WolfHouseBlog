package com.wolfhouse.wolfhouseblog.service;

import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;

import java.util.List;

public interface ArticleActionService {
    List<ArticleCommentVo> getArticleCommentVos(Long articleId);

    List<ArticleCommentVo> postComment(ArticleCommentDto dto);

    List<ArticleCommentVo> deleteComment(Long commentId);

    Boolean isLiked(Long articleId);

    Boolean like(Long articleId);

    Boolean dislike(Long articleId);

    Boolean isFavorite(Long articleId);

    Boolean favorite(Long articleId);

    Boolean removeFavorite(Long articleId);

}

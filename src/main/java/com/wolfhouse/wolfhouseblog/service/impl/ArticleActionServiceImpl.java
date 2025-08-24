package com.wolfhouse.wolfhouseblog.service.impl;

import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;
import com.wolfhouse.wolfhouseblog.service.ArticleActionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleActionServiceImpl implements ArticleActionService {
    // TODO 实现功能
    @Override
    public List<ArticleCommentVo> getArticleCommentVos(Long articleId) {
        return List.of();
    }

    @Override
    public List<ArticleCommentVo> postComment(ArticleCommentDto dto) {
        return List.of();
    }

    @Override
    public List<ArticleCommentVo> deleteComment(Long commentId) {
        return List.of();
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

package com.wolfhouse.wolfhouseblog.service;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleCommentPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;

import java.util.List;

public interface ArticleActionService {
    /**
     * 获取指定文章的评论列表
     *
     * @param dto 文章评论分页查询参数
     * @return 分页评论列表数据
     */
    PageResult<ArticleCommentVo> getArticleCommentVos(ArticleCommentPageDto dto);

    /**
     * 发表新评论
     *
     * @param dto 评论内容数据传输对象
     * @return 更新后的评论分页列表
     */
    PageResult<ArticleCommentVo> postComment(ArticleCommentDto dto);

    /**
     * 删除指定评论
     *
     * @param commentId 需要删除的评论ID
     * @return 更新后的评论分页列表
     */
    PageResult<ArticleCommentVo> deleteComment(Long commentId);

    Boolean isLiked(Long articleId);

    Boolean like(Long articleId);

    Boolean dislike(Long articleId);

    Boolean isFavorite(Long articleId);

    Boolean favorite(Long articleId);

    Boolean removeFavorite(Long articleId);
}

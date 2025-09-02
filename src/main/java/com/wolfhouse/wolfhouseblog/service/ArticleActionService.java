package com.wolfhouse.wolfhouseblog.service;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.*;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;

import java.util.List;

/**
 * 文章交互服务接口
 *
 * @author rylinwolf
 */
public interface ArticleActionService {
    /**
     * 检查指定文章的评论是否存在
     *
     * @param articleId 目标文章ID
     * @param commentId 目标评论ID
     * @return true表示评论存在，false表示评论不存在
     */
    Boolean isArticleCommentExist(Long articleId, Long commentId);

    /**
     * 获取指定文章的评论列表，支持分页查询
     *
     * @param dto 文章评论分页查询参数对象
     * @return 评论列表分页数据
     * @throws Exception 获取评论失败时抛出异常
     */
    PageResult<ArticleCommentVo> getArticleCommentVos(ArticleCommentQueryDto dto) throws Exception;

    /**
     * 获取指定文章的所有评论列表
     *
     * @param articleId 文章ID
     * @return 评论列表分页数据
     * @throws Exception 获取评论失败时抛出异常
     */
    PageResult<ArticleCommentVo> getArticleCommentVosByArticle(Long articleId) throws Exception;

    /**
     * 发表新评论到指定文章
     *
     * @param dto 评论内容数据传输对象
     * @return 更新后的评论分页列表
     * @throws Exception 发表评论失败时抛出异常
     */
    PageResult<ArticleCommentVo> postComment(ArticleCommentDto dto) throws Exception;

    /**
     * 删除指定评论
     *
     * @param dto 评论删除数据传输对象
     * @return 更新后的评论分页列表
     * * @throws Exception 未登录
     */
    PageResult<ArticleCommentVo> deleteComment(ArticleCommentDeleteDto dto) throws Exception;

    /**
     * 检查当前登录用户是否已对指定文章点赞
     *
     * @param articleId 目标文章ID
     * @return true表示已点赞，false表示未点赞
     * @throws Exception 未登录
     */
    Boolean isLiked(Long articleId) throws Exception;

    /**
     * 当前登录用户对指定文章进行点赞
     *
     * @param articleId 目标文章ID
     * @return true表示点赞成功，false表示点赞失败
     * @throws Exception 未登录
     */
    Boolean like(Long articleId) throws Exception;

    /**
     * 当前登录用户取消对指定文章的点赞
     *
     * @param articleId 目标文章ID
     * @return true表示取消成功，false表示取消失败
     * @throws Exception 未登录
     */
    Boolean dislike(Long articleId) throws Exception;

    /**
     * 获取指定文章的收藏夹列表
     *
     * @param articleId 目标文章ID
     * @return 收藏信息列表
     * @throws Exception 未登录
     */
    List<ArticleFavoriteVo> getFavoritesByArticle(Long articleId) throws Exception;

    /**
     * 获取指定收藏夹中的文章简要信息列表。
     * 支持分页查询指定收藏夹中收藏的所有文章的简要信息。
     *
     * @param dto 收藏夹分页查询参数对象，包含收藏夹ID、分页信息等
     * @return 文章简要信息列表分页数据
     */
    PageResult<ArticleBriefVo> getFavoritesArticle(ArticleFavoritePageDto dto) throws Exception;

    /**
     * 当前登录用户收藏指定文章
     *
     * @param articleId 目标文章ID
     * @return true表示收藏成功，false表示收藏失败
     */
    Boolean favorite(Long articleId);

    /**
     * 取消收藏指定文章
     *
     * @param articleId 目标文章ID
     * @return true表示取消成功，false表示取消失败
     */
    Boolean removeFavorite(Long articleId);

    /**
     * 清空指定收藏夹中的文章。
     *
     * @param favoritesId 收藏夹ID
     * @return true表示取消成功，false表示取消失败
     */
    Boolean removeFavorites(Long favoritesId);

}

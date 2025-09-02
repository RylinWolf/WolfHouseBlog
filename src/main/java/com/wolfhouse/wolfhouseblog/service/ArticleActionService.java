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
     * 用于验证某条评论在指定文章下是否存在
     *
     * @param articleId 目标文章ID
     * @param commentId 目标评论ID
     * @return true表示评论存在，false表示评论不存在
     */
    Boolean isArticleCommentExist(Long articleId, Long commentId);

    /**
     * 获取指定文章的评论列表，支持分页查询
     * 根据传入的查询参数获取文章评论列表，包含评论者信息、评论内容等
     *
     * @param dto 文章评论分页查询参数对象
     * @return 评论列表分页数据，包含评论详细信息
     * @throws Exception 获取评论失败时抛出异常，如数据库访问错误
     */
    PageResult<ArticleCommentVo> getArticleCommentVos(ArticleCommentQueryDto dto) throws Exception;

    /**
     * 获取指定文章的所有评论列表
     * 获取文章下的所有评论，不进行分页，按时间倒序排列
     *
     * @param articleId 文章ID
     * @return 评论列表分页数据，包含所有评论信息
     * @throws Exception 获取评论失败时抛出异常，如数据库访问错误
     */
    PageResult<ArticleCommentVo> getArticleCommentVosByArticle(Long articleId) throws Exception;

    /**
     * 发表新评论到指定文章
     * 将用户的评论内容保存到数据库，并返回更新后的评论列表
     *
     * @param dto 评论内容数据传输对象，包含评论内容、文章ID等信息
     * @return 更新后的评论分页列表，包含新发表的评论
     * @throws Exception 发表评论失败时抛出异常，如未登录或权限不足
     */
    PageResult<ArticleCommentVo> postComment(ArticleCommentDto dto) throws Exception;

    /**
     * 删除指定评论
     * 删除用户或管理员有权限删除的评论，仅评论作者和管理员可执行此操作
     *
     * @param dto 评论删除数据传输对象，包含待删除的评论ID等信息
     * @return 更新后的评论分页列表，已删除目标评论
     * @throws Exception 删除失败时抛出异常，如未登录或权限不足
     */
    PageResult<ArticleCommentVo> deleteComment(ArticleCommentDeleteDto dto) throws Exception;

    /**
     * 检查当前登录用户是否已对指定文章点赞
     * 查询当前用户是否在该文章的点赞记录中
     *
     * @param articleId 目标文章ID
     * @return true表示已点赞，false表示未点赞
     * @throws Exception 检查失败时抛出异常，如用户未登录
     */
    Boolean isLiked(Long articleId) throws Exception;

    /**
     * 当前登录用户对指定文章进行点赞
     * 为文章添加点赞记录，同时更新文章的点赞计数
     *
     * @param articleId 目标文章ID
     * @return true表示点赞成功，false表示点赞失败（如已点赞）
     * @throws Exception 点赞失败时抛出异常，如用户未登录
     */
    Boolean like(Long articleId) throws Exception;

    /**
     * 当前登录用户取消对指定文章的点赞
     * 移除用户的点赞记录，并更新文章点赞计数
     *
     * @param articleId 目标文章ID
     * @return true表示取消成功，false表示取消失败（如未点赞）
     * @throws Exception 取消失败时抛出异常，如用户未登录
     */
    Boolean unlike(Long articleId) throws Exception;

    /**
     * 获取指定文章的收藏夹列表
     * 查询当前文章被收藏到哪些收藏夹中，包含收藏夹的基本信息
     *
     * @param articleId 目标文章ID
     * @return 收藏信息列表，包含收藏夹名称、创建者等信息
     * @throws Exception 查询失败时抛出异常，如用户未登录
     */
    List<ArticleFavoriteVo> getFavoritesByArticle(Long articleId) throws Exception;

    /**
     * 获取指定收藏夹中的文章简要信息列表
     * 分页查询收藏夹中的所有文章，返回文章的基本信息
     *
     * @param dto 收藏夹分页查询参数对象，包含收藏夹ID、分页信息等
     * @return 文章简要信息列表分页数据，包含文章标题、作者、发布时间等
     * @throws Exception 查询失败时抛出异常，如收藏夹不存在
     */
    PageResult<ArticleBriefVo> getFavoritesArticle(ArticleFavoritePageDto dto) throws Exception;

    /**
     * 检查指定的文章是否存在于某个收藏夹中。
     *
     * @param dto 包含文章ID和收藏夹ID的文章收藏数据传输对象
     * @return true表示文章已在指定收藏夹中，false表示文章不在指定收藏夹中
     */
    Boolean isFavoriteExist(ArticleFavoriteDto dto);

    /**
     * 当前登录用户收藏指定文章
     * 将文章添加到指定的收藏夹中。如果收藏夹不存在或没有权限，将返回失败。
     *
     * @param dto 收藏信息对象，包含文章ID和目标收藏夹ID
     * @return true表示收藏成功，false表示收藏失败
     * @throws Exception 未登录或收藏夹非本人创建
     */
    Boolean favorite(ArticleFavoriteDto dto) throws Exception;


    /**
     * 取消收藏指定文章
     * 从指定的收藏夹中移除文章。如果收藏夹不存在或没有权限，将返回失败。
     *
     * @param dto 取消收藏信息对象，包含文章ID和目标收藏夹ID
     * @return true表示取消成功，false表示取消失败
     * @throws Exception 未登录或收藏夹非本人创建
     */
    Boolean removeFavorite(ArticleFavoriteDto dto) throws Exception;

    /**
     * 清空指定收藏夹中的所有文章
     * 将删除指定收藏夹中的所有已收藏文章。此操作不可逆，请谨慎使用。
     *
     * @param favoritesId 收藏夹ID
     * @return true表示清空成功，false表示清空失败
     * @throws Exception 未登录或收藏夹非本人创建
     */
    Boolean removeFavorites(Long favoritesId) throws Exception;

}

package com.wolfhouse.wolfhouseblog.auth.service;

import com.wolfhouse.wolfhouseblog.service.*;
import com.wolfhouse.wolfhouseblog.service.impl.PartitionServiceImpl;

/**
 * @author linexsong
 */
public interface ServiceAuthMediator {
    /**
     * 注册管理员服务实现
     *
     * @param adminService 要注册的管理员服务
     */
    void registerAdmin(AdminService adminService);

    /**
     * 注册用户服务实现
     *
     * @param userService 要注册的用户服务
     */
    void registerUser(UserService userService);

    /**
     * 注册用户认证服务实现
     *
     * @param userAuthService 要注册的用户认证服务
     */
    void registerUserAuth(UserAuthService userAuthService);

    /**
     * 注册文章服务实现
     *
     * @param articleService 要注册的文章服务
     */
    void registerArticle(ArticleService articleService);

    /**
     * 注册文章操作服务实现
     *
     * @param articleActionService 要注册的文章操作服务
     */
    void registerAction(ArticleActionService articleActionService);

    /**
     * 注册收藏夹服务实现
     *
     * @param favoritesService 要注册的收藏夹服务
     */
    void registerFavorite(FavoritesService favoritesService);

    /**
     * 注册分区服务实现。
     *
     * @param partitionService 要注册的分区服务
     */
    void registerPartition(PartitionServiceImpl partitionService);


    /**
     * 检查用户认证信息是否存在
     *
     * @param userId 用户ID
     * @return 如果认证信息存在返回true，否则返回false
     */
    Boolean isAuthExist(Long userId);

    /**
     * 检查用户是否已被删除
     *
     * @param userId 用户ID
     * @return 如果用户已删除返回true，否则返回false
     */
    Boolean isUserDeleted(Long userId);

    /**
     * 检查用户是否已启用
     *
     * @param userId 用户ID
     * @return 如果用户已启用返回true，否则返回false
     */
    Boolean isUserEnabled(Long userId);

    /**
     * 判断用户是否不可达
     *
     * @param userId 用户ID
     * @return 如果用户不可达则返回true，否则返回false
     */
    Boolean isUserUnaccessible(Long userId);

    /**
     * 验证用户凭证是否正确
     *
     * @param password 密码
     * @param userId   用户ID
     * @return 是否正确
     */
    Boolean verifyPassword(Long userId, String password);

    /**
     * 查询用户是否为管理员
     *
     * @param userId 用户 ID
     * @return 是否为管理员
     */
    Boolean isUserAdmin(Long userId);

    /**
     * 检查管理员是否存在
     *
     * @param adminId 管理员ID
     * @return 如果管理员存在返回true，否则返回false
     */
    Boolean isAdminExist(Long adminId);

    /**
     * 检查权限ID是否存在
     *
     * @param authorityIds 权限ID数组
     * @return 权限是否存在
     */
    Boolean isAuthoritiesExist(Long... authorityIds);

    /**
     * 检查指定的账号或邮箱是否已被使用
     *
     * @param s 要检查的账号或邮箱
     * @return 如果已存在返回true，不存在返回false
     */
    Boolean hasAccountOrEmail(String s);

    /**
     * 获取当前登录用户ID，如果未登录则抛出异常
     *
     * @return 当前登录用户ID
     * @throws Exception 未登录或登录用户不可达
     */
    Long loginUserOrE() throws Exception;

    /**
     * 检查指定用户是否可以访问文章
     *
     * @param userId    用户ID
     * @param articleId 文章ID
     * @return 如果可以访问返回true，否则返回false
     * @throws Exception 未登录异常
     */
    Boolean isArticleReachable(Long userId, Long articleId) throws Exception;

    /**
     * 检查文章评论是否存在
     *
     * @param articleId 文章ID
     * @param commentId 评论ID
     * @return 如果评论存在返回true，否则返回false
     */
    Boolean isArticleCommentExist(Long articleId, Long commentId);

    /**
     * 检查用户是否为文章作者
     *
     * @param articleId 文章ID
     * @param login     用户ID
     * @return 如果是作者返回true，否则返回false
     */
    Boolean isArticleOwner(Long articleId, Long login);

    /**
     * 检查收藏夹标题是否已存在
     *
     * @param title 收藏夹标题
     * @return 如果已存在返回true，否则返回false
     * @throws Exception 未登录异常
     */
    Boolean isFavoritesTitleExist(String title) throws Exception;

    /**
     * 检查当前登录用户是否拥有指定收藏夹
     *
     * @param id 收藏夹ID
     * @return 如果拥有返回true，否则返回false
     * @throws Exception 未登录异常
     */
    Boolean isFavoritesIdOwn(Long id) throws Exception;

    /**
     * 检查指定用户的分区是否存在。
     *
     * @param userId      用户ID
     * @param partitionId 分区ID
     * @return 如果分区存在返回true，否则返回false
     * @throws Exception 未登录异常
     */
    Boolean isUserPartitionExist(Long userId, Long partitionId) throws Exception;

    /**
     * 检查指定用户的分区名称是否存在。
     *
     * @param userId        用户ID
     * @param partitionName 分区名称
     * @return 如果分区名称存在返回true，否则返回false
     */
    Boolean isUserPartitionNameExist(Long userId, String partitionName);

}

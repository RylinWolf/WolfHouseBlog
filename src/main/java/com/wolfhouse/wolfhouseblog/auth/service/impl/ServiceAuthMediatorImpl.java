package com.wolfhouse.wolfhouseblog.auth.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.service.*;
import com.wolfhouse.wolfhouseblog.service.impl.PartitionServiceImpl;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.AdminTableDef.ADMIN;

/**
 * @author rylinwolf
 */
@Component("authMediator")
public class ServiceAuthMediatorImpl implements ServiceAuthMediator {
    private UserAuthService authService;
    private AdminService adminService;
    private UserService userService;
    private ArticleService articleService;
    private ArticleActionService actionService;
    private FavoritesService favoritesService;
    private PartitionService partitionService;

    @Override
    public void registerFavorite(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Override
    public void registerPartition(PartitionServiceImpl partitionService) {
        this.partitionService = partitionService;
    }

    @Override
    public void registerAdmin(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void registerUser(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void registerUserAuth(UserAuthService userAuthService) {
        this.authService = userAuthService;
    }

    @Override
    public void registerArticle(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void registerAction(ArticleActionService articleActionService) {
        this.actionService = articleActionService;
    }

    @Override
    public Boolean isAuthExist(Long userId) {
        return authService.isAuthExist(userId);
    }

    @Override
    public Boolean isUserDeleted(Long userId) {
        return authService.isUserDeleted(userId);
    }

    @Override
    public Boolean isUserEnabled(Long userId) {
        return authService.isUserEnabled(userId);
    }

    @Override
    public Boolean isUserUnaccessible(Long userId) {
        return authService.isUserUnaccessible(userId);
    }

    @Override
    public Boolean verifyPassword(Long userId, String password) {
        return authService.verifyPassword(password, userId);
    }

    @Override
    public Boolean isUserAdmin(Long userId) {
        return adminService.isUserAdmin(userId);
    }

    @Override
    public Boolean isAuthoritiesExist(Long... authorityIds) {
        return adminService.isAuthoritiesExist(authorityIds);
    }

    @Override
    public Boolean hasAccountOrEmail(String s) {
        return userService.hasAccountOrEmail(s);
    }

    @Override
    public Boolean isAdminExist(Long adminId) {
        return adminService.exists(QueryWrapper.create()
                                               .where(ADMIN.ID.eq(adminId)));
    }

    @Override
    @NonNull
    public Long loginUserOrE() throws Exception {
        return authService.loginUserOrE();
    }

    @Override
    public Long loginUserOrNull() {
        try {
            return authService.loginUserOrE();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean isArticleReachable(Long userId, Long articleId) throws Exception {
        return articleService.isArticleReachable(userId, articleId);
    }

    @Override
    public Boolean isArticleCommentExist(Long articleId, Long commentId) {
        return actionService.isArticleCommentExist(articleId, commentId);
    }

    @Override
    public Boolean isArticleOwner(Long articleId, Long login) {
        return articleService.isArticleOwner(articleId, login);
    }

    @Override
    public Boolean isFavoritesTitleExist(String title) throws Exception {
        return favoritesService.isFavoritesTitleExist(title);
    }

    @Override
    public Boolean isFavoritesIdOwn(Long id) throws Exception {
        return favoritesService.isFavoritesIdOwn(id);
    }

    @Override
    public Boolean isUserPartitionExist(Long userId, Long partitionId) throws Exception {
        return partitionService.isUserPartitionExist(userId, partitionId);
    }

    @Override
    public Boolean isUserPartitionNameExist(Long userId, String partitionName) {
        return partitionService.isUserPartitionNameExist(userId, partitionName);
    }
}

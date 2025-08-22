package com.wolfhouse.wolfhouseblog.auth.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import org.springframework.stereotype.Component;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.AdminTableDef.ADMIN;

/**
 * @author linexsong
 */
@Component("authMediator")
public class ServiceAuthMediatorImpl implements ServiceAuthMediator {
    private UserAuthService authService;
    private AdminService adminService;
    private UserService userService;

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
    public Long loginUserOrE() throws Exception {
        return authService.loginUserOrE();
    }
}

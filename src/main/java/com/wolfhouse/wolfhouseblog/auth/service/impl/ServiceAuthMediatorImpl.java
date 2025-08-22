package com.wolfhouse.wolfhouseblog.auth.service.impl;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import org.springframework.stereotype.Component;

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
        return null;
    }

    @Override
    public Boolean isUserDeleted(Long userId) {
        return null;
    }

    @Override
    public Boolean isUserEnabled(Long userId) {
        return null;
    }

    @Override
    public Boolean isUserUnaccessible(Long userId) {
        return null;
    }

    @Override
    public Boolean verifyPassword(Long userId, String password) {
        return null;
    }

    @Override
    public Boolean isUserAdmin(Long userId) {
        return null;
    }

    @Override
    public Boolean isAuthoritiesExist(Long... authorityIds) {
        return null;
    }

    @Override
    public Boolean hasAccountOrEmail(String s) {
        return null;
    }
}

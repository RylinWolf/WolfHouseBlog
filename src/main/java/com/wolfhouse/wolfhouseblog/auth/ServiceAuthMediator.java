package com.wolfhouse.wolfhouseblog.auth;

import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component("authMediator")
@RequiredArgsConstructor
public class ServiceAuthMediator {
    private final UserAuthService authService;
    private final AdminService adminService;
    private final UserService userService;
}

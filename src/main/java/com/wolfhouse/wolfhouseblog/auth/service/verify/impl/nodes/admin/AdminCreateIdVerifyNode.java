package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;

/**
 * @author linexsong
 */
public class AdminCreateIdVerifyNode extends BaseVerifyNode<Long> {
    private final AdminService service;
    private final UserAuthService authService;

    public AdminCreateIdVerifyNode(AdminService service, UserAuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @Override
    public boolean verify() {
        this.customException = new ServiceException(AdminConstant.ADMIN_EXISTS);
        return !authService.isUserUnaccessible(t) && !service.isUserAdmin(t);
    }
}

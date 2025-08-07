package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.service.AdminService;

/**
 * @author linexsong
 */
public class AdminUserIdVerifyNode extends BaseVerifyNode<Long> {
    private final AdminService service;

    public AdminUserIdVerifyNode(AdminService service) {
        this.service = service;
    }

    @Override
    public boolean verify() {
        this.customException = new ServiceException(AdminConstant.ADMIN_NOT_EXIST);
        return service.isUserAdmin(t);
    }
}

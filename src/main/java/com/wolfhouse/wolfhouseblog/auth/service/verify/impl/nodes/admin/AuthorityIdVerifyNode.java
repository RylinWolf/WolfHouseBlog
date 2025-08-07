package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.service.AdminService;

/**
 * @author linexsong
 */
public class AuthorityIdVerifyNode extends BaseVerifyNode<Long[]> {
    private final AdminService service;

    public AuthorityIdVerifyNode(AdminService service) {
        this.service = service;
    }

    @Override
    public boolean verify() {
        if (t == null || t.length == 0) {
            return true;
        }

        this.customException = new ServiceException(AdminConstant.AUTHORITIES_NOT_EXIST);
        return service.isAuthoritiesExist(t);
    }
}

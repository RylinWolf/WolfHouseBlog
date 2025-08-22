package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;

/**
 * @author linexsong
 */
public class AuthorityIdVerifyNode extends BaseVerifyNode<Long[]> {
    private final ServiceAuthMediator mediator;

    public AuthorityIdVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean verify() {
        if (t == null || t.length == 0) {
            return true;
        }

        this.customException = new ServiceException(AdminConstant.AUTHORITIES_NOT_EXIST);
        return mediator.isAuthoritiesExist(t);
    }
}

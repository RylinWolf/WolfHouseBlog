package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

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
        if (t == null) {
            return allowNull;
        }

        if (t.length == 0) {
            return true;
        }

        this.customException = new ServiceException(AdminConstant.AUTHORITIES_NOT_EXIST);
        return mediator.isAuthoritiesExist(t);
    }
}

package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class AdminCreateIdVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public AdminCreateIdVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean verify() {
        this.customException = new ServiceException(AdminConstant.ADMIN_EXISTS);
        return !mediator.isUserUnaccessible(t) && !mediator.isUserAdmin(t);
    }
}

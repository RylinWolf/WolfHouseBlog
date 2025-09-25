package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class AdminIdVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public AdminIdVerifyNode(ServiceAuthMediator mediator) {
        super();
        this.mediator = mediator;
    }

    @Override
    public boolean verify() {
        this.customException = new ServiceException(AdminConstant.ADMIN_NOT_EXIST);
        return t != null && mediator.isAdminExist(t);
    }
}

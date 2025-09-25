package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class PartitionIdVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public PartitionIdVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
    }

    public PartitionIdVerifyNode(Long aLong, ServiceAuthMediator mediator) {
        super(aLong);
        this.mediator = mediator;
    }

    public PartitionIdVerifyNode(Long aLong, Boolean allowNull, ServiceAuthMediator mediator) {
        super(aLong, allowNull);
        this.mediator = mediator;
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }
        this.customException = new ServiceException(PartitionConstant.NOT_EXIST);
        try {
            return super.verify() && mediator.isUserPartitionExist(mediator.loginUserOrE(), t);
        } catch (Exception e) {
            return false;
        }
    }
}

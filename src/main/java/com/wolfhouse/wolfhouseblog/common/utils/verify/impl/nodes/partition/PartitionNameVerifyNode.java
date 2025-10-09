package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class PartitionNameVerifyNode extends BaseVerifyNode<String> {
    private final ServiceAuthMediator mediator;
    private Long login;

    public PartitionNameVerifyNode(ServiceAuthMediator mediator) {
        super();
        this.mediator = mediator;
    }

    public PartitionNameVerifyNode(ServiceAuthMediator mediator, String t) {
        this(mediator);
        this.t = t;
    }

    public PartitionNameVerifyNode(ServiceAuthMediator mediator, String t, Boolean allowNull) {
        this(mediator, t);
        this.allowNull = allowNull;
    }

    public PartitionNameVerifyNode login(Long login) {
        this.login = login;
        return this;
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }
        // 分区已存在
        try {
            this.exception(new ServiceException(PartitionConstant.ALREADY_EXIST));
            if (mediator.isUserPartitionNameExist(login, this.t)) {
                return false;
            }
        } catch (Exception e) {
            this.exception(new ServiceException(PartitionConstant.ALREADY_EXIST + e));
            return false;
        }
        this.exception(VerifyConstant.VERIFY_FAILED + "[name]");
        return super.verify() &&
               new StringVerifyNode(1L, 20L, allowNull)
                   .target(this.t)
                   .verify();
    }
}

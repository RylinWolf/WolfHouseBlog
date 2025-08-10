package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.service.PartitionService;

/**
 * @author linexsong
 */
public class PartitionIdVerifyNode extends BaseVerifyNode<Long> {
    private final PartitionService service;

    public PartitionIdVerifyNode(PartitionService service) {
        this.service = service;
    }

    public PartitionIdVerifyNode(Long aLong, PartitionService service) {
        super(aLong);
        this.service = service;
    }

    public PartitionIdVerifyNode(Long aLong, Boolean allowNull, PartitionService service) {
        super(aLong, allowNull);
        this.service = service;
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }

        return super.verify() && service.isUserPartitionExist(ServiceUtil.loginUserOrE(), t);
    }
}

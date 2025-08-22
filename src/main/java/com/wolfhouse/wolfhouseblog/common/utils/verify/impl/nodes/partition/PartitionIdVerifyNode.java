package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
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
        this.customException = new ServiceException(PartitionConstant.NOT_EXIST);
        try {
            return super.verify() && service.isUserPartitionExist(ServiceUtil.loginUserOrE(), t);
        } catch (Exception e) {
            return false;
        }
    }
}

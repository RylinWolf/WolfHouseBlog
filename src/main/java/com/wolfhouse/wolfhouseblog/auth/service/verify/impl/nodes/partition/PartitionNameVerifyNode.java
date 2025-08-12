package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.service.PartitionService;

/**
 * @author linexsong
 */
public class PartitionNameVerifyNode extends BaseVerifyNode<String> {
    private final PartitionService service;

    public PartitionNameVerifyNode(PartitionService service) {
        this.service = service;
    }

    public PartitionNameVerifyNode(PartitionService service, String s) {
        super(s);
        this.service = service;
    }

    public PartitionNameVerifyNode(PartitionService service, String s, Boolean allowNull) {
        super(s, allowNull);
        this.service = service;
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }
        // 分区已存在
        try {
            if (service.getPartitionVoByName(this.t) != null) {
                this.customException =
                     customException == null ? new ServiceException(PartitionConstant.ALREADY_EXIST)
                                             : customException;
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
        return super.verify() && new StringVerifyNode(1L, 10L, allowNull).verify();
    }
}

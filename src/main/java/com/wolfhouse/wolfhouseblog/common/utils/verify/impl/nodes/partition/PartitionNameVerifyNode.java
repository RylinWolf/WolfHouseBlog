package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.service.PartitionService;

/**
 * @author linexsong
 */
public class PartitionNameVerifyNode extends BaseVerifyNode<String> {
    private final PartitionService service;
    private Long login;

    public PartitionNameVerifyNode(PartitionService service) {
        super();
        this.service = service;
    }

    public PartitionNameVerifyNode(PartitionService service, String t) {
        this(service);
        this.t = t;
    }

    public PartitionNameVerifyNode(PartitionService service, String t, Boolean allowNull) {
        this(service, t);
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
            if (service.exists(QueryWrapper.create()
                                           .eq(Partition::getUserId, login)
                                           .eq(Partition::getName, this.t))) {

                return false;
            }
        } catch (Exception e) {
            this.exception(new ServiceException(PartitionConstant.ALREADY_EXIST + e));
            return false;
        }
        this.exception(VerifyConstant.VERIFY_FAILED + "[name]");
        return super.verify() &&
               new StringVerifyNode(1L, 10L, allowNull)
                    .target(this.t)
                    .verify();
    }
}

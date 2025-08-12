package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.service.PartitionService;

/**
 * @author linexsong
 */
public class PartitionNameVerifyNode extends BaseVerifyNode<String> {
    private final PartitionService service;
    private Long login;

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
            if (service.exists(QueryWrapper.create()
                                           .eq(Partition::getUserId, login)
                                           .eq(Partition::getName, this.t))) {
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

package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;

import java.util.Map;
import java.util.Set;

/**
 * @author linexsong
 */
public class PartitionIdLoopVerifyNode extends BaseVerifyNode<Long> {
    private Map<Long, Set<Long>> idMap;
    private Long parent;

    public PartitionIdLoopVerifyNode() {
        super();
        this.customException = new ServiceException(PartitionConstant.LOOP);
    }

    public PartitionIdLoopVerifyNode idMap(Map<Long, Set<Long>> idMap) {
        this.idMap = idMap;
        return this;
    }

    public PartitionIdLoopVerifyNode parent(Long parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public PartitionIdLoopVerifyNode target(Long target) {
        this.t = target;
        return this;
    }

    @Override
    public boolean verify() {
        if (parent == null && allowNull) {
            return true;
        }
        if (idMap == null || t == null || parent == null || t.equals(parent)) {
            return false;
        }

        // 获取当前节点的孩子节点，若包含新的父节点，则成环
        return !idMap.get(t)
                     .contains(parent);
    }
}

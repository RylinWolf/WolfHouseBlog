package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.StringVerifyNode;

/**
 * @author linexsong
 */
public class PartitionNameVerifyNode extends BaseVerifyNode<String> {
    public PartitionNameVerifyNode() {
    }

    public PartitionNameVerifyNode(String s) {
        super(s);
    }

    public PartitionNameVerifyNode(String s, Boolean allowNull) {
        super(s, allowNull);
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }
        return super.verify() && new StringVerifyNode(1L, 10L, allowNull).verify();
    }
}

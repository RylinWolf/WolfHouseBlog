package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

import java.util.Arrays;

/**
 * @author linexsong
 */
public class PartitionVisibleVerifyNode extends BaseVerifyNode<Integer> {
    public PartitionVisibleVerifyNode() {
    }

    public PartitionVisibleVerifyNode(Integer integer) {
        super(integer);
    }

    public PartitionVisibleVerifyNode(Integer integer, Boolean allowNull) {
        super(integer, allowNull);
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }

        return super.verify() && Arrays.stream(VisibilityEnum.values())
                                       .anyMatch(e -> e.value.equals(this.t));
    }
}

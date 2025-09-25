package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class PartitionVerifyNode {
    public static final PartitionVisibleVerifyNode VISIBILITY = new PartitionVisibleVerifyNode();
    public static final PartitionIdLoopVerifyNode ID_LOOP = new PartitionIdLoopVerifyNode();

    private static PartitionIdVerifyNode ID;
    private static PartitionNameVerifyNode NAME;

    public static PartitionIdVerifyNode id(ServiceAuthMediator mediator) {
        if (ID == null) {
            ID = new PartitionIdVerifyNode(mediator);
        }
        return ID;
    }

    public static PartitionNameVerifyNode name(ServiceAuthMediator mediator) {
        if (NAME == null) {
            NAME = new PartitionNameVerifyNode(mediator);
        }
        return NAME;
    }
}

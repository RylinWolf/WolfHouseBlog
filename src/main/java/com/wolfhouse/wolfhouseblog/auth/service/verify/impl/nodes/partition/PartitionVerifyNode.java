package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.service.PartitionService;

/**
 * @author linexsong
 */
public class PartitionVerifyNode {
    public static final PartitionVisibleVerifyNode VISIBILITY = new PartitionVisibleVerifyNode();

    private static PartitionIdVerifyNode ID;
    private static PartitionNameVerifyNode NAME;

    public static PartitionIdVerifyNode id(PartitionService service) {
        if (ID == null) {
            ID = new PartitionIdVerifyNode(service);
        }
        return ID;
    }

    public static PartitionNameVerifyNode name(PartitionService service) {
        if (NAME == null) {
            NAME = new PartitionNameVerifyNode(service);
        }
        return NAME;
    }
}

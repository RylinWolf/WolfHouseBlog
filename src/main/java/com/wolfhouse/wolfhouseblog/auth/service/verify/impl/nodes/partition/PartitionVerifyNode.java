package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition;

import com.wolfhouse.wolfhouseblog.service.PartitionService;

/**
 * @author linexsong
 */
public class PartitionVerifyNode {
    public static final PartitionNameVerifyNode NAME = new PartitionNameVerifyNode();
    private static PartitionIdVerifyNode ID;

    public static PartitionIdVerifyNode id(PartitionService service) {
        if (ID == null) {
            ID = new PartitionIdVerifyNode(service);
        }
        return ID;
    }
}

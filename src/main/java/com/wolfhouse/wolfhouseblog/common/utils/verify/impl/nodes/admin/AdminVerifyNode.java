package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;

/**
 * @author linexsong
 */
public final class AdminVerifyNode {
    public static final AdminNameVerifyNode NAME = new AdminNameVerifyNode();

    private static AdminCreateIdVerifyNode CREATE_ID;
    private static AdminIdVerifyNode ID;
    private static AdminUserIdVerifyNode USER_ID;
    private static AuthorityIdVerifyNode AUTHORITY_ID;

    public static AdminCreateIdVerifyNode createId(ServiceAuthMediator mediator) {
        if (CREATE_ID == null) {
            CREATE_ID = new AdminCreateIdVerifyNode(mediator);
        }
        return CREATE_ID;
    }

    public static AdminIdVerifyNode id(ServiceAuthMediator mediator) {
        if (ID == null) {
            ID = new AdminIdVerifyNode(mediator);
        }
        return ID;
    }

    public static AdminUserIdVerifyNode userId(ServiceAuthMediator mediator) {
        if (USER_ID == null) {
            USER_ID = new AdminUserIdVerifyNode(mediator);
        }
        return USER_ID;
    }

    public static AuthorityIdVerifyNode authorityId(ServiceAuthMediator mediator) {
        if (AUTHORITY_ID == null) {
            AUTHORITY_ID = new AuthorityIdVerifyNode(mediator);
        }
        return AUTHORITY_ID;
    }

}

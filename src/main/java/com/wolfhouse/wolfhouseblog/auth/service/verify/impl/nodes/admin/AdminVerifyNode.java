package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;

/**
 * @author linexsong
 */
public final class AdminVerifyNode {
    public static final AdminNameVerifyNode NAME = new AdminNameVerifyNode();

    private static AdminCreateIdVerifyNode CREATE_ID;
    private static AdminIdVerifyNode ID;
    private static AdminUserIdVerifyNode USER_ID;
    private static AuthorityIdVerifyNode AUTHORITY_ID;

    public static AdminCreateIdVerifyNode createId(AdminService s1, UserAuthService s2) {
        if (CREATE_ID == null) {
            CREATE_ID = new AdminCreateIdVerifyNode(s1, s2);
        }
        return CREATE_ID;
    }

    public static AdminIdVerifyNode id(AdminService service) {
        if (ID == null) {
            ID = new AdminIdVerifyNode(service);
        }
        return ID;
    }

    public static AdminUserIdVerifyNode userId(AdminService service) {
        if (USER_ID == null) {
            USER_ID = new AdminUserIdVerifyNode(service);
        }
        return USER_ID;
    }

    public static AuthorityIdVerifyNode authorityId(AdminService service) {
        if (AUTHORITY_ID == null) {
            AUTHORITY_ID = new AuthorityIdVerifyNode(service);
        }
        return AUTHORITY_ID;
    }

}

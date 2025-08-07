package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;

/**
 * @author linexsong
 */
public final class AdminVerifyNode {
    private static AdminCreateIdVerifyNode ADMIN_VERIFY_NODE;

    public static AdminCreateIdVerifyNode id(AdminService s1, UserAuthService s2) {
        if (ADMIN_VERIFY_NODE == null) {
            ADMIN_VERIFY_NODE = new AdminCreateIdVerifyNode(s1, s2);
        }
        return ADMIN_VERIFY_NODE;
    }

}

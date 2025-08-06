package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;

/**
 * @author linexsong
 */
public class UserVerifyNode {
    public static final UsernameVerifyNode USERNAME = new UsernameVerifyNode();
    public static final BirthVerifyNode BIRTH = new BirthVerifyNode();
    private static UserIdVerifyNode USER_ID;
    private static EmailVerifyNode EMAIL;
    
    public static EmailVerifyNode email(UserService service) {
        if (EMAIL == null) {
            EMAIL = new EmailVerifyNode(service);
        }
        return EMAIL;
    }

    public static UserIdVerifyNode id(UserAuthService service) {
        if (USER_ID == null) {
            USER_ID = new UserIdVerifyNode(service);
        }
        return USER_ID;
    }
}

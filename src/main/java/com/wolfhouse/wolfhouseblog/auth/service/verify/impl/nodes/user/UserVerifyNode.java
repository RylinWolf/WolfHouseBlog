package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.service.UserService;

/**
 * @author linexsong
 */
public class UserVerifyNode {
    public static final UsernameVerifyNode USERNAME = new UsernameVerifyNode();
    public static final BirthVerifyNode BIRTH = new BirthVerifyNode();
    private static UserIdVerifyNode USER_ID;
    private static EmailVerifyNode EMAIL;
    private static UserPwdAuthVerifyNode PWD;

    public static EmailVerifyNode email(UserService service) {
        if (EMAIL == null) {
            EMAIL = new EmailVerifyNode(service);
        }
        return EMAIL;
    }

    public static UserIdVerifyNode id(ServiceAuthMediator mediator) {
        if (USER_ID == null) {
            USER_ID = new UserIdVerifyNode(mediator);
        }
        return USER_ID;
    }

    public static UserPwdAuthVerifyNode pwd(ServiceAuthMediator mediator) {
        if (PWD == null) {
            PWD = new UserPwdAuthVerifyNode(mediator);
        }
        return PWD;
    }

    public void clear() {
        EMAIL = null;
        USER_ID = null;
        PWD = null;
    }
}

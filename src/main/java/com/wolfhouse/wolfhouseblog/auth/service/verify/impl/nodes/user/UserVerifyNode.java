package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.service.UserService;

/**
 * @author linexsong
 */
public class UserVerifyNode {
    public static final UsernameVerifyNode USERNAME = new UsernameVerifyNode();
    public static final BirthVerifyNode BIRTH = new BirthVerifyNode();
    public static EmailVerifyNode EMAIL;

    public static EmailVerifyNode email(UserService service) {
        if (EMAIL == null) {
            EMAIL = new EmailVerifyNode(service);
        }
        return EMAIL;
    }
}

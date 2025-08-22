package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * @author linexsong
 */
@RequiredArgsConstructor
public class EmailVerifyNode extends BaseVerifyNode<String> {
    private final UserService service;

    @Override
    public boolean verify() {
        super.verify();
        String email = this.t.toLowerCase();

        try {
            return service.getUserVoById(ServiceUtil.loginUser())
                          .getEmail()
                          .equals(email) || !service.hasAccountOrEmail(email);
        } catch (Exception ignored) {
            return false;
        }
    }
}

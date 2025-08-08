package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;

/**
 * @author linexsong
 */
public class UserPwdAuthVerifyNode extends BaseVerifyNode<String> {
    private final UserAuthService authService;
    public Long userId;

    public UserPwdAuthVerifyNode(UserAuthService authService) {
        this.customException = new ServiceException(AuthExceptionConstant.AUTHENTIC_FAILED);
        this.authService = authService;
    }

    public UserPwdAuthVerifyNode userId(Long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean verify() {
        if (t == null || userId == null) {
            return false;
        }
        return authService.verifyPassword(t, userId);
    }
}

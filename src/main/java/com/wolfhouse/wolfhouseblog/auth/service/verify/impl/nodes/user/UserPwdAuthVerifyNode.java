package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;

/**
 * @author linexsong
 */
public class UserPwdAuthVerifyNode extends BaseVerifyNode<String> {
    private final ServiceAuthMediator mediator;
    public Long userId;

    public UserPwdAuthVerifyNode(ServiceAuthMediator mediator) {
        this.customException = new ServiceException(AuthExceptionConstant.AUTHENTIC_FAILED);
        this.mediator = mediator;
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
        return mediator.verifyPassword(userId, t);
    }
}

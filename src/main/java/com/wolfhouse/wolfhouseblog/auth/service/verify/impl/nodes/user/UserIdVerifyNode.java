package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;

/**
 * @author linexsong
 */
public class UserIdVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public UserIdVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
    }

    public UserIdVerifyNode(ServiceAuthMediator media, Long aLong) {
        this(media);
        this.t = aLong;
    }

    public UserIdVerifyNode(ServiceAuthMediator service, Long aLong, Boolean allowNull) {
        this(service, aLong);
        this.allowNull = allowNull;
    }

    @Override
    public boolean verify() {
        if (allowNull && this.t == null) {
            return true;
        }

        // 用户不存在或已删除
        if (!mediator.isAuthExist(this.t) || mediator.isUserDeleted(this.t)) {
            this.customException = new ServiceException(UserConstant.USER_NOT_EXIST);
            return false;
        }

        // 用户被禁用
        if (!mediator.isUserEnabled(this.t)) {
            this.customException = new ServiceException(UserConstant.USER_HAS_BEEN_BANNED);
            return false;
        }

        return true;
    }
}

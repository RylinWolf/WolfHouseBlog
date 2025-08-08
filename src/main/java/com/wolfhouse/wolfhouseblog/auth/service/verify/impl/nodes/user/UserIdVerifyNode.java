package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;

/**
 * @author linexsong
 */
public class UserIdVerifyNode extends BaseVerifyNode<Long> {
    private final UserAuthService service;

    public UserIdVerifyNode(UserAuthService service) {
        this.service = service;
    }

    public UserIdVerifyNode(UserAuthService service, Long aLong) {
        this(service);
        this.t = aLong;
    }

    public UserIdVerifyNode(UserAuthService service, Long aLong, Boolean allowNull) {
        this(service, aLong);
        this.allowNull = allowNull;
    }

    @Override
    public boolean verify() {
        if (allowNull && this.t == null) {
            return true;
        }

        // 用户不存在或已删除
        if (!service.isAuthExist(this.t) || service.isUserDeleted(this.t)) {
            this.customException = new ServiceException(UserConstant.USER_NOT_EXIST);
            return false;
        }

        // 用户被禁用
        if (!service.isUserEnabled(this.t)) {
            this.customException = new ServiceException(UserConstant.USER_HAS_BEEN_BANNED);
            return false;
        }

        return true;
    }
}

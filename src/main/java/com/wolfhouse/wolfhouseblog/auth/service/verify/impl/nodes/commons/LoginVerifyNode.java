package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;

/**
 * @author linexsong
 */
public class LoginVerifyNode extends BaseVerifyNode<Long> {
    public LoginVerifyNode() {
        this.customException = ServiceException.loginRequired();
    }

    @Override
    public boolean verify() {
        return ServiceUtil.isLogin() && super.verify();
    }
}

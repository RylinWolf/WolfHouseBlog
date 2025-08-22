package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons;

import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

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

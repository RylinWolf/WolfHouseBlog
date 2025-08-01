package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;

/**
 * @author linexsong
 */
public class LoginVerifyNode extends BaseVerifyNode<Long> {
    @Override
    public boolean verify() {
        return ServiceUtil.isLogin() && super.verify();
    }
}

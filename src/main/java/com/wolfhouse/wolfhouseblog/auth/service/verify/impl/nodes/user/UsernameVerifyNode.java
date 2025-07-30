package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class UsernameVerifyNode extends BaseVerifyNode<String> {
    @Override
    public boolean verify() {
        super.verify();
        int length = this.t.length();
        return length >= 2 && length <= 20;
    }
}

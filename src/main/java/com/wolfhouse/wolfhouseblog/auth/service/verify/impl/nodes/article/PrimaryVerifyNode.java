package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.StringVerifyNode;

/**
 * @author linexsong
 */
public class PrimaryVerifyNode extends BaseVerifyNode<String> {
    public PrimaryVerifyNode() {
    }

    public PrimaryVerifyNode(String s) {
        super(s);
    }

    public PrimaryVerifyNode(String s, Boolean allowNull) {
        super(s, allowNull);
    }

    @Override
    public boolean verify() {
        return super.verify() && new StringVerifyNode(0L, 50L, true).target(t)
                                                                    .verify();
    }
}

package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons.StringVerifyNode;

/**
 * @author linexsong
 */
public class PrimaryVerifyNode extends BaseVerifyNode<String> {
    public PrimaryVerifyNode() {
    }

    public PrimaryVerifyNode(String s) {
        super(s);
    }

    @Override
    public boolean verify() {
        super.verify();
        return new StringVerifyNode(0L, 50L, true).target(t)
                                                  .verify();
    }
}

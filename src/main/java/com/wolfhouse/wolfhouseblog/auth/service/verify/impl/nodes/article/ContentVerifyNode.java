package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.StringVerifyNode;

/**
 * @author linexsong
 */
public class ContentVerifyNode extends BaseVerifyNode<String> {
    public ContentVerifyNode() {
    }

    public ContentVerifyNode(String s) {
        super(s);
    }

    public ContentVerifyNode(String s, Boolean allowNull) {
        super(s, allowNull);
    }

    @Override
    public boolean verify() {
        return super.verify() && new StringVerifyNode(1L, 2000L, allowNull)
                .target(t)
                .verify();
    }
}

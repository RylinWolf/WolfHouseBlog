package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons.StringVerifyNode;

/**
 * @author linexsong
 */
public class ContentVerifyNode extends BaseVerifyNode<String> {
    public ContentVerifyNode() {
    }

    public ContentVerifyNode(String s) {
        super(s);
    }

    @Override
    public boolean verify() {
        return new StringVerifyNode(0L, 2000L, false)
                .target(t)
                .verify();
    }
}

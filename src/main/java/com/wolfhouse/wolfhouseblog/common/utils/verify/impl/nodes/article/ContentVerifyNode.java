package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.StringVerifyNode;

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
        return super.verify() && new StringVerifyNode(1L, 5000L, allowNull)
            .target(t)
            .verify();
    }
}

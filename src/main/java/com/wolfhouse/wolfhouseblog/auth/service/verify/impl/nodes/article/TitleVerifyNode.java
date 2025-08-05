package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons.StringVerifyNode;

/**
 * @author linexsong
 */
public class TitleVerifyNode extends BaseVerifyNode<String> {
    public TitleVerifyNode() {
    }

    public TitleVerifyNode(String s) {
        super(s);
    }

    public TitleVerifyNode(String s, Boolean allowNull) {
        super(s, allowNull);
    }

    @Override
    public boolean verify() {
        return super.verify() && new StringVerifyNode(1L, 20L, allowNull).target(t)
                                                                         .verify();
    }
}

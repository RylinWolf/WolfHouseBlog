package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.tag;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.StringVerifyNode;

/**
 * @author linexsong
 */
public class TagNameVerifyNode extends StringVerifyNode {
    public TagNameVerifyNode() {
        this(1L, 20L, false);
    }

    public TagNameVerifyNode(Long min, Long max, Boolean allowNull) {
        super(min, max, allowNull);
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }
        return super.verify();
    }
}

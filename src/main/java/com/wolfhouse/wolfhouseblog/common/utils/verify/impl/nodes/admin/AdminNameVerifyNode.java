package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.admin;

import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.StringVerifyNode;

/**
 * @author linexsong
 */
public class AdminNameVerifyNode extends StringVerifyNode {

    public AdminNameVerifyNode() {
        this(2L, 20L, true);
    }

    public AdminNameVerifyNode(Long min, Long max, Boolean allowNull) {
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

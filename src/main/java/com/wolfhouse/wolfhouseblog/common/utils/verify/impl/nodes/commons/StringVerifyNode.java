package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons;

import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class StringVerifyNode extends BaseVerifyNode<String> {
    protected final Long min;
    protected final Long max;
    protected final Boolean allowNull;

    public StringVerifyNode(Long min, Long max, Boolean allowNull) {
        this.min = min;
        this.max = max;
        this.allowNull = allowNull;
    }

    @Override
    public boolean verify() {
        if (allowNull && this.t == null) {
            return true;
        }

        int length = this.t == null ? 0 : this.t.length();
        return super.verify() && length >= this.min && length <= this.max;
    }
}

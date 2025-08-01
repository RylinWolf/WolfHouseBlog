package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;

/**
 * @author linexsong
 */
public class StringVerifyNode extends BaseVerifyNode<String> {
    private final Long min;
    private final Long max;
    private final Boolean allowNull;

    public StringVerifyNode(Long min, Long max, Boolean allowNull) {
        this.min = min;
        this.max = max;
        this.allowNull = allowNull;
    }

    @Override
    public boolean verify() {
        super.verify();
        if (!allowNull && BeanUtil.isBlank(this.t)) {
            return false;
        }

        int length = this.t == null ? 0 : this.t.length();
        return length >= this.min && length <= this.max;
    }
}

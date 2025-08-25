package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons;

import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class NotAnyBlankVerifyNode extends BaseVerifyNode<Object[]> {
    {
        this.customException = new VerifyException(VerifyConstant.NOT_ANY_BLANK);
    }

    public NotAnyBlankVerifyNode() {
    }

    public NotAnyBlankVerifyNode(Object... objects) {
        super();
        this.t = objects;
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return false;
        }
        return !BeanUtil.isAnyBlank(t);
    }
}

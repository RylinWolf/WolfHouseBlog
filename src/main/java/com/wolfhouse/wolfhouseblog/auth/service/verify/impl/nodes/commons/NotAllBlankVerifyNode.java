package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons;

import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author linexsong
 */
public class NotAllBlankVerifyNode extends BaseVerifyNode<Object[]> {
    {
        this.customException = new VerifyException(VerifyConstant.NOT_ALL_BLANK);
    }

    public NotAllBlankVerifyNode() {
    }

    public NotAllBlankVerifyNode(Object... objects) {
        super(objects);
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return false;
        }
        
        boolean allBlank = true;
        Iterator<?> iter = Arrays.stream(t)
                                 .iterator();
        while (iter.hasNext()) {
            if (iter.next() != null) {
                allBlank = false;
                break;
            }
        }
        return !allBlank;
    }
}

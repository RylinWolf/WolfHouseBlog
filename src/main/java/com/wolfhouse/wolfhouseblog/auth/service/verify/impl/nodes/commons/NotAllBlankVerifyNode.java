package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons;

import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author linexsong
 */
public class NotAllBlankVerifyNode extends BaseVerifyNode<Object[]> {
    {
        this.customException = new ServiceException(VerifyConstant.NOT_ALL_BLANK);
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
            Object next = iter.next();
            // 非空则 allBlank 为 false
            if (next != null) {
                if (JsonNullable.class.isAssignableFrom(next.getClass())) {
                    var nullable = (JsonNullable<?>) next;
                    // nullable 无数据
                    if (!nullable.isPresent()) {
                        continue;
                    }
                }
                allBlank = false;
                break;
            }
        }
        return !allBlank;
    }
}

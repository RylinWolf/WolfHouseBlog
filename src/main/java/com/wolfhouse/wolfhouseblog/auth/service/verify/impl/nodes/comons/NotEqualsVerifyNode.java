package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class NotEqualsVerifyNode<T> extends BaseVerifyNode<T> {
    private final T t2;

    public NotEqualsVerifyNode(T t, T t2) {
        super(t);
        this.t2 = t2;
    }

    @Override
    public boolean verify() {
        return super.verify() && !this.t.equals(this.t2);
    }
}

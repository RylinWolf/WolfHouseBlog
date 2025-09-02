package com.wolfhouse.wolfhouseblog.common.utils.verify.impl;

/**
 * @author linexsong
 */
public final class EmptyVerifyNode<T> extends BaseVerifyNode<T> {
    @Override
    public EmptyVerifyNode<T> target(T target) {
        super.target(target);
        return this;
    }

    public static <T> EmptyVerifyNode<T> of(T t) {
        return new EmptyVerifyNode<T>().target(t);
    }
}

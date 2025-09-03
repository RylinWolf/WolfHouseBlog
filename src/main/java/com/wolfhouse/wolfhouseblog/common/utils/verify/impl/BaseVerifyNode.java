package com.wolfhouse.wolfhouseblog.common.utils.verify.impl;

import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyStrategy;

import java.util.function.Predicate;

/**
 * @author linexsong
 */
public abstract class BaseVerifyNode<T> implements VerifyNode<T> {
    protected T t;
    protected Predicate<T> predicate;
    protected Exception customException;
    protected VerifyStrategy strategy = VerifyStrategy.WITH_CUSTOM_EXCEPTION;
    protected Boolean allowNull = false;

    public BaseVerifyNode() {
    }

    public BaseVerifyNode(T t) {
        this();
        this.t = t;
    }

    public BaseVerifyNode(T t, Boolean allowNull) {
        this(t);
        this.allowNull = allowNull;
    }

    @Override
    public BaseVerifyNode<T> predicate(Predicate<T> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public BaseVerifyNode<T> exception(Exception e) {
        this.customException = e;
        return this;
    }

    @Override
    public BaseVerifyNode<T> exception(String message) {
        this.customException = new VerifyException(message);
        return this;
    }

    @Override
    public BaseVerifyNode<T> allowNull(Boolean allowNull) {
        this.allowNull = allowNull;
        return this;
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }

        if (predicate != null) {
            return predicate.test(t);
        }

        return true;
    }

    @Override
    public boolean verify(Predicate<T> predicate) {
        return (this.allowNull == true && t == null) || predicate.test(t);
    }


    @Override
    public boolean verifyWithCustomE(Exception e) throws Exception {
        this.customException = e;
        if (!verify()) {
            throw customException;
        }
        return true;
    }


    @Override
    public boolean verifyWithCustomE() throws Exception {
        if (customException == null) {
            return verifyWithE();
        }
        return verifyWithCustomE(customException);
    }

    @Override
    public boolean verifyWithE() throws Exception {
        return verifyWithCustomE(new VerifyException(VerifyConstant.VERIFY_FAILED));
    }

    @Override
    public BaseVerifyNode<T> setStrategy(VerifyStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public BaseVerifyNode<T> setCustomException(Exception e) {
        this.customException = e;
        return this;
    }

    @Override
    public VerifyStrategy getStrategy() {
        return this.strategy;
    }

    @Override
    public Exception getException() {
        return this.customException;
    }

    @Override
    public BaseVerifyNode<T> target(T target) {
        this.t = target;
        return this;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

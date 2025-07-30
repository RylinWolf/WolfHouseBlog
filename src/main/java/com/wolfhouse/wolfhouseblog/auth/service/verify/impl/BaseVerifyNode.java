package com.wolfhouse.wolfhouseblog.auth.service.verify.impl;

import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyStrategy;

import java.util.function.Predicate;

/**
 * @author linexsong
 */
public abstract class BaseVerifyNode<T> implements VerifyNode<T> {
    protected T t;
    protected Predicate<T> predicate;
    protected Exception customException;
    protected VerifyStrategy strategy;

    @Override
    public VerifyNode<T> predicate(Predicate<T> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public VerifyNode<T> exception(Exception e) {
        this.customException = e;
        return this;
    }

    @Override
    public boolean verify() {
        if (predicate != null) {
            return predicate.test(t);
        }
        return false;
    }

    @Override
    public boolean verify(Predicate<T> predicate) {
        return predicate.test(t);
    }


    @Override
    public boolean verifyWithCustomE(Exception e) throws Exception {
        if (!verify()) {
            throw e;
        }
        return true;
    }


    @Override
    public boolean verifyWithCustomE() throws Exception {
        return verifyWithCustomE(customException);
    }

    @Override
    public boolean verifyWithE() throws Exception {
        return verifyWithCustomE(new VerifyException(VerifyConstant.VERIFY_FAILED));
    }

    @Override
    public void setStrategy(VerifyStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void setCustomException(Exception e) {
        this.customException = e;
    }

    @Override
    public void setVerifyBy(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Exception getCustomException() {
        return this.customException;
    }

    @Override
    public VerifyStrategy getStrategy() {
        return this.strategy;
    }

    @Override
    public VerifyNode<T> target(T target) {
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

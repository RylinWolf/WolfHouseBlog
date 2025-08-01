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
    protected VerifyStrategy strategy = VerifyStrategy.WITH_CUSTOM_EXCEPTION;

    public BaseVerifyNode() {
    }

    public BaseVerifyNode(T t) {
        this.t = t;
    }

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
    public VerifyNode<T> exception(String message) {
        this.customException = new VerifyException(message);
        return this;
    }

    @Override
    public boolean verify() {
        if (predicate != null) {
            return predicate.test(t);
        }
        return true;
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
    public VerifyNode<T> setStrategy(VerifyStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public VerifyNode<T> setCustomException(Exception e) {
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

package com.wolfhouse.wolfhouseblog.auth.service.verify.impl;

import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyChain;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author linexsong
 */
public class BaseVerifyChain implements VerifyChain {
    private final Set<VerifyNode<?>> nodes;
    private final List<String> failed;

    public BaseVerifyChain() {
        nodes = new HashSet<>();
        failed = new ArrayList<>();
    }

    @Override
    public <T> boolean add(VerifyNode<T> node, Predicate<T> predicate) {
        node.setVerifyBy(predicate);
        return add(node);
    }

    @Override
    public <T> boolean add(VerifyNode<T> node) {
        return nodes.add(node);
    }

    @Override
    public <T> boolean add(T t, Predicate<T> predicate) {
        return nodes.add(new BaseVerifyNode<>() {
            {
                target(t);
                predicate(predicate);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof BaseVerifyNode<?> node) {
                    return node.predicate.equals(predicate) && node.t.equals(t);
                }
                return false;
            }

            @Override
            public int hashCode() {
                return t.hashCode() + predicate.hashCode();
            }
        });
    }

    @Override
    public <T> boolean remove(VerifyNode<T> node) {
        return nodes.remove(node);
    }

    @Override
    public boolean doVerify() throws Exception {
        for (VerifyNode<?> n : nodes) {
            var v = switch (n.getStrategy()) {
                case NORMAL -> n.verify();
                case WITH_EXCEPTION -> n.verifyWithE();
                case WITH_CUSTOM_EXCEPTION -> n.verifyWithCustomE();
            };
            if (!v) {
                failed.add(n.toString());
            }
        }
        return failed.isEmpty();
    }

    @Override
    public List<String> failed() {
        return this.failed;
    }
}

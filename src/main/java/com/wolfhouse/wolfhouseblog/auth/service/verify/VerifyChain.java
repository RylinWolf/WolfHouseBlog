package com.wolfhouse.wolfhouseblog.auth.service.verify;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author linexsong
 */
public interface VerifyChain<T> {
    /**
     * 添加验证节点
     *
     * @param node 节点
     */
    boolean add(VerifyNode<T> node);

    boolean add(VerifyNode<T> node, Predicate<T> predicate);

    boolean add(T t, Predicate<T> predicate);

    /**
     * 移除验证节点
     *
     * @param node 要移除的验证节点
     */
    boolean remove(VerifyNode<T> node);

    /**
     * 执行验证链中的所有验证
     */
    boolean doVerify() throws Exception;

    List<String> failed();
}

package com.wolfhouse.wolfhouseblog.auth.service.verify;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyChain;

/**
 * @author linexsong
 */
public class VerifyTool {
    public static BaseVerifyChain of(VerifyNode<?>... nodes) {
        BaseVerifyChain chain = BaseVerifyChain.instance();
        chain.add(nodes);
        return chain;
    }

    public static BaseVerifyChain ofAllMsg(String msg, VerifyNode<?>... nodes) {
        for (VerifyNode<?> node : nodes) {
            if (node.getException() == null) {
                node.exception(msg);
            }
        }
        return of(nodes);
    }
}

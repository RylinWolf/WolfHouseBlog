package com.wolfhouse.wolfhouseblog.auth.service.verify;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyChain;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons.LoginVerifyNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author linexsong
 */
public class VerifyTool {
    /**
     * 根据 VerifyNode 数组构建验证链
     *
     * @param nodes 验证节点数组
     * @return 验证链
     */
    public static BaseVerifyChain of(VerifyNode<?>... nodes) {
        BaseVerifyChain chain = BaseVerifyChain.instance();
        chain.add(nodes);
        return chain;
    }

    /**
     * 根据 VerifyNode 数组构建验证链，并设置统一错误信息。
     * 该错误信息的优先级低于 VerifyNode 配置的信息。
     *
     * @param nodes 验证节点数组
     * @param msg   错误信息
     * @return 验证链
     */
    public static BaseVerifyChain ofAllMsg(String msg, VerifyNode<?>... nodes) {
        for (VerifyNode<?> node : nodes) {
            if (node.getException() == null) {
                node.exception(msg);
            }
        }
        return of(nodes);
    }

    /**
     * 根据 VerifyNode 数组构建验证链。
     * 自动添加登陆验证节点。
     *
     * @param nodes 验证节点数组
     * @return 验证链
     */
    public static BaseVerifyChain ofLogin(VerifyNode<?>... nodes) {
        List<VerifyNode<?>> nodeList = new ArrayList<>(Arrays.stream(nodes)
                                                             .toList());
        nodeList.addFirst(new LoginVerifyNode());
        return of(nodeList.toArray(new VerifyNode[0]));
    }
}

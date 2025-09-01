package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class IdOwnVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public IdOwnVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return allowNull;
        }
        Long login;
        try {
            login = mediator.loginUserOrE();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }

        return mediator.isArticleOwner(t, login);
    }
}

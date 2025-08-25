package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class IdReachableVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public IdReachableVerifyNode(ServiceAuthMediator mediator) {
        super();
        this.mediator = mediator;
        this.customException = ServiceException.notAllowed();
    }

    @Override
    public IdReachableVerifyNode target(Long target) {
        this.t = target;
        return this;
    }

    @Override
    public boolean verify() {
        try {
            Long login = mediator.loginUserOrE();
            return mediator.isArticleReachable(login, t);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}

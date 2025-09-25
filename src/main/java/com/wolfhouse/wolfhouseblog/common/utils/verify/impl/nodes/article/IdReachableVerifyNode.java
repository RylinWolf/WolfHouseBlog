package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class IdReachableVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public IdReachableVerifyNode(ServiceAuthMediator mediator) {
        super();
        this.mediator = mediator;
        this.customException = new ServiceException(ArticleConstant.ACCESS_DENIED);
    }

    @Override
    public IdReachableVerifyNode target(Long target) {
        this.t = target;
        return this;
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return false;
        }
        Long login = mediator.loginUserOrNull();
        try {
            return mediator.isArticleReachable(login, t);
        } catch (Exception e) {
            this.customException = new ServiceException(e.getMessage(), e);
            return false;
        }
    }
}

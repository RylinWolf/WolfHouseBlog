package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.services.FavoritesConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class FavoritesIdOwnVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;

    public FavoritesIdOwnVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
        this.customException = new ServiceException(FavoritesConstant.NOT_EXIST);
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return allowNull;
        }
        try {
            return mediator.isFavoritesIdOwn(t);
        } catch (Exception e) {
            return false;
        }
    }
}

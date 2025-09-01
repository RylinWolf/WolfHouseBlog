package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

/**
 * @author linexsong
 */
public class FavoritesTitleVerifyNode extends BaseVerifyNode<String> {
    private final ServiceAuthMediator mediator;

    public FavoritesTitleVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return allowNull;
        }
        try {
            return mediator.isFavoritesTitleExist(t);
        } catch (Exception e) {
            return false;
        }
    }
}

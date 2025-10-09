package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites;

import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.FavoritesTableDef.FAVORITES;

/**
 * @author linexsong
 */
public class FavoritesTitleVerifyNode extends BaseVerifyNode<String> {
    private final ServiceAuthMediator mediator;

    public FavoritesTitleVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
        this.customException = new VerifyException(FAVORITES.TITLE.getName());
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return allowNull;
        }
        try {
            return new StringVerifyNode(1L, 10L, allowNull)
                       .target(t)
                       .verify() &&
                   !mediator.isFavoritesTitleExist(t);
        } catch (Exception e) {
            return false;
        }
    }
}

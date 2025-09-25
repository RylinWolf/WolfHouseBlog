package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites;

import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class FavoritesVerifyNode {
    private static FavoritesTitleVerifyNode TITLE;
    private static FavoritesIdOwnVerifyNode ID;

    public static FavoritesTitleVerifyNode title(ServiceAuthMediator mediator) {
        if (TITLE == null) {
            TITLE = new FavoritesTitleVerifyNode(mediator);
        }
        return TITLE;
    }

    public static FavoritesIdOwnVerifyNode idOwn(ServiceAuthMediator mediator) {
        if (ID == null) {
            ID = new FavoritesIdOwnVerifyNode(mediator);
        }
        return ID;
    }
}

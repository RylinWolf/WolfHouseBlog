package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class FavoritesVerifyNode {
    private static FavoritesTitleVerifyNode TITLE;
    private static FavoritesIdVerifyNode ID;

    public static FavoritesTitleVerifyNode title(ServiceAuthMediator mediator) {
        if (TITLE == null) {
            TITLE = new FavoritesTitleVerifyNode(mediator);
        }
        return TITLE;
    }

    public static FavoritesIdVerifyNode id(ServiceAuthMediator mediator) {
        if (ID == null) {
            ID = new FavoritesIdVerifyNode(mediator);
        }
        return ID;
    }
}

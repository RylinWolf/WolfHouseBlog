package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class FavoritesVerifyNode {
    private static FavoritesTitleVerifyNode TITLE;

    public static FavoritesTitleVerifyNode title(ServiceAuthMediator mediator) {
        if (TITLE == null) {
            TITLE = new FavoritesTitleVerifyNode(mediator);
        }
        return TITLE;
    }
}

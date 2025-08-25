package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class ArticleVerifyNode {
    public static final ContentVerifyNode CONTENT = new ContentVerifyNode();
    public static final PrimaryVerifyNode PRIMARY = new PrimaryVerifyNode();
    public static final TitleVerifyNode TITLE = new TitleVerifyNode();
    private static IdReachableVerifyNode ID;

    public static IdReachableVerifyNode id(ServiceAuthMediator mediator) {
        if (ID == null) {
            ID = new IdReachableVerifyNode(mediator);
        }
        return ID;
    }

    public static ContentVerifyNode content(String t, Boolean allowNull) {
        return new ContentVerifyNode(t, allowNull);
    }

    public static PrimaryVerifyNode primary(String t, Boolean allowNull) {
        return new PrimaryVerifyNode(t, allowNull);
    }

    public static TitleVerifyNode title(String t, Boolean allowNull) {
        return new TitleVerifyNode(t, allowNull);
    }
}

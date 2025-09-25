package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;

/**
 * @author linexsong
 */
public class ArticleVerifyNode {
    public static final ContentVerifyNode CONTENT = new ContentVerifyNode();
    public static final PrimaryVerifyNode PRIMARY = new PrimaryVerifyNode();
    public static final TitleVerifyNode TITLE = new TitleVerifyNode();
    private static IdReachableVerifyNode ID_REACHABLE;
    private static IdOwnVerifyNode ID_OWNER;
    private static CommentIdVerifyNode COMMENT_ID;

    public static IdOwnVerifyNode idOwn(ServiceAuthMediator mediator) {
        if (ID_OWNER == null) {
            ID_OWNER = new IdOwnVerifyNode(mediator);
        }
        return ID_OWNER;
    }

    public static IdReachableVerifyNode idReachable(ServiceAuthMediator mediator) {
        if (ID_REACHABLE == null) {
            ID_REACHABLE = new IdReachableVerifyNode(mediator);
        }
        return ID_REACHABLE;
    }

    public static CommentIdVerifyNode commentId(ServiceAuthMediator mediator) {
        if (COMMENT_ID == null) {
            COMMENT_ID = new CommentIdVerifyNode(mediator);
        }
        return COMMENT_ID;
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

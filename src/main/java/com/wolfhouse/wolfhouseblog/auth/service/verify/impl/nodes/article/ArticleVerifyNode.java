package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.service.ArticleService;

/**
 * @author linexsong
 */
public class ArticleVerifyNode {
    public static final ContentVerifyNode CONTENT = new ContentVerifyNode();
    public static final PrimaryVerifyNode PRIMARY = new PrimaryVerifyNode();
    public static final TitleVerifyNode TITLE = new TitleVerifyNode();

    public static IdReachableVerifyNode id(Long id, ArticleService service) {
        return new IdReachableVerifyNode(id, service);
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

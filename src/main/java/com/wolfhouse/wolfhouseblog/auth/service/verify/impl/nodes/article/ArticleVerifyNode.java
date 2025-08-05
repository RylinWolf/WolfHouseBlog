package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.service.ArticleService;

/**
 * @author linexsong
 */
public class ArticleVerifyNode {
    public static final ContentVerifyNode CONTENT = new ContentVerifyNode();
    public static final PrimaryVerifyNode PRIMARY = new PrimaryVerifyNode();
    public static final TitleVerifyNode TITLE = new TitleVerifyNode();
    public static IdReachableVerifyNode ID;

    public IdReachableVerifyNode id(Long id, ArticleService service) {
        if (ID == null) {
            ID = new IdReachableVerifyNode(id, service);
        }
        return ID;
    }
}

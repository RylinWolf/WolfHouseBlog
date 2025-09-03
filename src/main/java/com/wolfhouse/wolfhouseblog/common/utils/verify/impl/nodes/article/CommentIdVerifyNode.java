package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

/**
 * @author ruixe
 */
public class CommentIdVerifyNode extends BaseVerifyNode<Long> {
    private final ServiceAuthMediator mediator;
    private Long articleId;

    public CommentIdVerifyNode(ServiceAuthMediator mediator) {
        this.mediator = mediator;
        this.customException = new ServiceException(ArticleConstant.COMMENT_NOT_EXIST);
    }

    public CommentIdVerifyNode articleId(Long articleId) {
        this.articleId = articleId;
        return this;
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return allowNull;
        }
        return mediator.isArticleCommentExist(articleId, t);
    }
}

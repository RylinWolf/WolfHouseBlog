package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.tag;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.service.TagService;

/**
 * @author linexsong
 */
public class TagIdVerifyNode extends BaseVerifyNode<Long> {
    private final TagService service;
    private Long userId;

    public TagIdVerifyNode(TagService service) {
        this.customException = ServiceException.notAllowed();
        this.service = service;
    }

    @Override
    public TagIdVerifyNode target(Long t) {
        this.t = t;
        return this;
    }

    public TagIdVerifyNode userId(Long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean verify() {
        if (allowNull && t == null) {
            return true;
        }

        return super.verify() && service.isUserTagExist(userId, this.t);
    }
}

package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.tag;

import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.TagService;

import java.util.Set;

/**
 * @author linexsong
 */
public class TagIdVerifyNode extends BaseVerifyNode<Long> {
    private final TagService service;
    private Long userId;
    private Set<Long> t;

    public TagIdVerifyNode(TagService service) {
        this.customException = ServiceException.notAllowed();
        this.service = service;
    }

    @Override
    public TagIdVerifyNode target(Long t) {
        this.t = Set.of(t);
        return this;
    }

    public TagIdVerifyNode target(Set<Long> t) {
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

        return super.verify() && service.isUserTagsExist(userId, this.t);
    }
}

package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.tag;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.StringVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.TagConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;

/**
 * @author linexsong
 */
public class TagNameVerifyNode extends StringVerifyNode {
    public TagNameVerifyNode() {
        this(1L, 20L, false);
    }

    public TagNameVerifyNode(Long min, Long max, Boolean allowNull) {
        super(min, max, allowNull);
        this.customException = new ServiceException(TagConstant.BAD_NAME_FORMAT);
    }

    @Override
    public boolean verify() {
        if (t == null && allowNull) {
            return true;
        }
        return super.verify();
    }
}

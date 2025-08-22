package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.common.constant.services.TagConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.TagService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author linexsong
 */
@Component
public class ComUseTagVerifyNode extends BaseVerifyNode<Set<Long>> {
    private final TagService service;
    private final UserAuthService authService;
    private Long userId;

    public ComUseTagVerifyNode(TagService service, UserAuthService authService) {
        this.service = service;
        this.authService = authService;
        this.customException = new ServiceException(TagConstant.NOT_EXIST);
    }

    public ComUseTagVerifyNode userId(Long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean verify() {
        if (t == null) {
            return allowNull;
        }
        if (t.isEmpty()) {
            return true;
        }

        if (this.userId == null) {
            try {
                this.userId = authService.loginUserOrE();
            } catch (Exception e) {
                throw new ServiceException(e.getMessage(), e);
            }
        }

        return super.verify() && service.isUserTagsExist(this.userId, this.t);
    }
}

package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.service.AdminService;

/**
 * @author linexsong
 */
public class AdminIdVerifyNode extends BaseVerifyNode<Long> {
    private final AdminService service;

    public AdminIdVerifyNode(AdminService service) {
        super();
        this.service = service;
    }

    @Override
    public boolean verify() {
        this.customException = new ServiceException(AdminConstant.ADMIN_NOT_EXIST);
        return t != null && service.exists(QueryWrapper.create()
                                                       .eq(Admin::getId, t));
    }
}

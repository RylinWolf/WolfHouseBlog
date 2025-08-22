package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user;

import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyStrategy;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;

import java.time.LocalDate;

/**
 * @author linexsong
 */
public class BirthVerifyNode extends BaseVerifyNode<LocalDate> {
    {
        this.strategy = VerifyStrategy.WITH_CUSTOM_EXCEPTION;
        this.customException = new VerifyException(String.format(
             "%s: 【%s】",
             UserConstant.VERIFY_FAILED,
             UserConstant.BIRTH));
    }

    @Override
    public boolean verify() {
        super.verify();

        return !this.t.isAfter(LocalDate.now());
    }
}

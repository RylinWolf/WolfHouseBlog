package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.wolfhouse.wolfhouseblog.botverify.model.VerifyResult;
import lombok.Data;

/**
 * 邮箱注册结果视图对象
 *
 * @author Rylin Wolf
 */
@Data
public class MailRegisterVo {
    private VerifyResult verifyResult;
}

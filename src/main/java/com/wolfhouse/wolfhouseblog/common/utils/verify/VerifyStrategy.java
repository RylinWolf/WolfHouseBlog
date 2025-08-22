package com.wolfhouse.wolfhouseblog.common.utils.verify;

/**
 * @author linexsong
 */
public enum VerifyStrategy {
    /** 仅验证，若失败则返回 false */
    NORMAL,
    /** 若验证失败，则抛出异常 */
    WITH_EXCEPTION,
    /** 若验证失败，则抛出自定义异常信息 */
    WITH_CUSTOM_EXCEPTION,


}

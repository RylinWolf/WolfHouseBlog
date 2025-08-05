package com.wolfhouse.wolfhouseblog.common.exceptions;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;

/**
 * @author linexsong
 */
public class ServiceException extends RuntimeException {
    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    protected ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static ServiceException processingFailed(String msg) {
        return new ServiceException(msg);
    }

    public static ServiceException loginRequired() {
        return new ServiceException(AuthExceptionConstant.LOGIN_REQUIRED);
    }

    public static ServiceException notAllowed() {
        return new ServiceException(AuthExceptionConstant.ACCESS_DENIED);
    }
}

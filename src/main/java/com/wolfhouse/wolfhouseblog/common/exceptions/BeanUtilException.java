package com.wolfhouse.wolfhouseblog.common.exceptions;

/**
 * @author linexsong
 */
public class BeanUtilException extends RuntimeException {
    public BeanUtilException() {
    }

    public BeanUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanUtilException(String message) {
        super(message);
    }

    public BeanUtilException(Throwable cause) {
        super(cause);
    }

    public static BeanUtilException propertiesCopyFailed(Throwable e) {
        return new BeanUtilException(e);
    }
}

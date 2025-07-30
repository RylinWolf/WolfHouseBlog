package com.wolfhouse.wolfhouseblog.auth.service.verify;

/**
 * @author linexsong
 */
public class VerifyException extends RuntimeException {
    public VerifyException(String message) {
        super(message);
    }

    public VerifyException() {
        super();
    }

}

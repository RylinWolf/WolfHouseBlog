package com.wolfhouse.wolfhouseblog.common.exceptions;

import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import lombok.Getter;

/**
 * @author linexsong
 */
public class UserAuthException extends ServiceException {
    @Getter
    public Long userId;
    public String message = UserConstant.USER_NOT_EXIST;

    public UserAuthException(Long userId) {
        super();
        this.userId = userId;
    }

    public UserAuthException(Throwable cause, Long userId) {
        super(cause);
        this.userId = userId;
    }

    public UserAuthException(String message, Throwable cause, Long userId) {
        super(message, cause);
        this.userId = userId;
    }

    public UserAuthException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }

    @Override
    public String toString() {
        return super.toString() + this.userId;
    }
}

package com.wolfhouse.wolfhouseblog.auth.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author rylinwolf
 */
public class AuthenticationJwtException extends AuthenticationException {


    public AuthenticationJwtException(String message) {
        super(message);
    }
}

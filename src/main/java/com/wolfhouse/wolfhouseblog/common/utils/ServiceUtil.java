package com.wolfhouse.wolfhouseblog.common.utils;

import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author linexsong
 */
public class ServiceUtil {
    public static Long loginUser() {
        Authentication auth = SecurityContextHolder.getContext()
                                                   .getAuthentication();
        if (AnonymousAuthenticationToken.class.isAssignableFrom(auth.getClass())) {
            return null;
        }
        return (Long) auth.getPrincipal();
    }

    public static Boolean isLogin() {
        return loginUser() != null;
    }

    public static Long loginUserOrE() {
        Long l = loginUser();
        if (l == null) {
            throw ServiceException.loginRequired();
        }
        return l;
    }
}

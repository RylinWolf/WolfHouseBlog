package com.wolfhouse.wolfhouseblog.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author linexsong
 */
public class ServiceUtil {
    public static Long loginUser() {
        return (Long) SecurityContextHolder.getContext()
                                           .getAuthentication()
                                           .getPrincipal();
    }

    public static Boolean isLogin() {
        Authentication auth = SecurityContextHolder.getContext()
                                                   .getAuthentication();
        return auth != null && auth.isAuthenticated();
    }
}

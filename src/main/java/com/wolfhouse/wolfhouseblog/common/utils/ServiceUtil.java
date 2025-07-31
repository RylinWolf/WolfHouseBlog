package com.wolfhouse.wolfhouseblog.common.utils;

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
}

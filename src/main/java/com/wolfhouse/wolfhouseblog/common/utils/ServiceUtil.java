package com.wolfhouse.wolfhouseblog.common.utils;

import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

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

    public static Authentication getAuth() {
        return SecurityContextHolder.getContext()
                                    .getAuthentication();
    }

    public static void setLoginUser(Long loginId) {
        SecurityContext context = SecurityContextHolder.getContext();
        
        context.setAuthentication(new UsernamePasswordAuthenticationToken(loginId, null));
    }

    public static void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();

        if (auth == null) {
            auth = new UsernamePasswordAuthenticationToken(null, null, authorities);
        }
        context.setAuthentication(auth);
    }
}

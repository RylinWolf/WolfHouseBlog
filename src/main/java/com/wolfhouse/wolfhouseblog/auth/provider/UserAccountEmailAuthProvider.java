package com.wolfhouse.wolfhouseblog.auth.provider;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class UserAccountEmailAuthProvider implements AuthenticationProvider {
    private final UserAuthService authService;
    private final UserService userService;
    private final AdminService adminService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取用户
        String accountOrEmail = authentication.getName();
        Optional<User> userO = userService.findByAccountOrEmail(accountOrEmail);
        var user = userO.orElseThrow(() -> new UsernameNotFoundException(AuthExceptionConstant.AUTHENTIC_FAILED));

        String password = authentication.getCredentials()
                                        .toString();
        var userId = user.getId();
        // 验证用户密码
        Boolean isVerified = authService.verifyPassword(password, userId);

        if (!isVerified) {
            throw new AuthenticationCredentialsNotFoundException(AuthExceptionConstant.AUTHENTIC_FAILED);
        }

        // 获取权限
        List<Authority> authorities = Collections.emptyList();
        if (adminService.isUserAdmin(userId)) {
            authorities = adminService.getAuthorities(userId);
        }

        return new UsernamePasswordAuthenticationToken(userId, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

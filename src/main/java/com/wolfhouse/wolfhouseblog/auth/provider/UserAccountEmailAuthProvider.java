package com.wolfhouse.wolfhouseblog.auth.provider;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserAccountEmailAuthProvider implements AuthenticationProvider {
    private final UserAuthService authService;
    private final UserService userService;
    private final AdminService adminService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        // 获取用户
        String accountOrEmail = authentication.getName();
        Optional<User> userO;
        userO = Optional.ofNullable(userService.findByAccountOrEmail(accountOrEmail));

        var user = userO.orElseThrow(() -> new UsernameNotFoundException(AuthExceptionConstant.AUTHENTIC_FAILED));

        String password = authentication.getCredentials()
                                        .toString();
        var userId = user.getId();
        // 验证用户密码
        Boolean isVerified = authService.verifyPassword(password, userId);

        if (!isVerified) {
            throw new AuthenticationCredentialsNotFoundException(AuthExceptionConstant.AUTHENTIC_FAILED);
        }

        // 验证用户是否可用
        try {
            VerifyTool.of(UserVerifyNode.id(authService)
                                        .target(userId))
                      .doVerify();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }

        // 获取权限
        List<Authority> authorities = Collections.emptyList();
        if (adminService.isUserAdmin(userId)) {
            try {
                authorities = adminService.getAuthorities(userId);
            } catch (Exception ignored) {
                log.error("{}, 【{}】", ServiceExceptionConstant.SERVER_ERROR, "加载权限失败");
            }
        }


        return new UsernamePasswordAuthenticationToken(
             userId,
             password,
             authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

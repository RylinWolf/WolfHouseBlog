package com.wolfhouse.wolfhouseblog.auth.filter;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.http.HttpConstant;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AdminService adminService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        String token = req.getHeader(HttpConstant.AUTH_HEADER);

        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            // 获取用户
            Optional<User> userO = Optional.ofNullable(userService.findByUserId(userId));
            userO.orElseThrow(() -> new ServiceException(AuthExceptionConstant.AUTHENTIC_FAILED));

            log.info("JWT 信息: {}, 过期时间: {}", userId, claims.getExpiration());

            // 获取用户权限
            List<Authority> authorities = adminService.getAuthorities(userId);
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);

            // 保留验证信息
            SecurityContextHolder.getContext()
                                 .setAuthentication(auth);
        } catch (Exception ignored) {} finally {
            filterChain.doFilter(request, response);
        }
    }
}

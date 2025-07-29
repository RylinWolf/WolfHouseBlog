package com.wolfhouse.wolfhouseblog.auth.filter;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.http.HttpConstant;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.services.AdminService;
import com.wolfhouse.wolfhouseblog.services.UserService;
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
            String account = claims.getSubject();
            // 获取用户
            Optional<User> userO = userService.findByAccountOrEmail(account);
            var user = userO.orElseThrow(() -> new ServiceException(AuthExceptionConstant.AUTHENTIC_FAILED));

            log.info("JWT 登录: {}, 过期时间: {}", account, claims.getExpiration());

            // 获取用户权限
            List<Authority> authorities = adminService.getAuthorities(user.getId());
            Authentication auth = new UsernamePasswordAuthenticationToken(account, null, authorities);

            // 保留验证信息
            SecurityContextHolder.getContext()
                                 .setAuthentication(auth);
            
        } catch (Exception ignored) {} finally {
            filterChain.doFilter(request, response);
        }
    }
}

package com.wolfhouse.wolfhouseblog.auth.filter;

import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.http.HttpConstant;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import io.jsonwebtoken.Claims;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * @author linexsong
 */
@RequiredArgsConstructor
@Slf4j
@NonNullApi
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AdminService adminService;
    private final UserAuthService authService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
         throws IOException, ServletException {
        String token = request.getHeader(HttpConstant.AUTH_HEADER);

        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            // 验证用户是否可达
            VerifyTool.ofLoginExist(authService)
                      .doVerify();

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

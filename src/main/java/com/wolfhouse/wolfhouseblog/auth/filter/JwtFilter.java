package com.wolfhouse.wolfhouseblog.auth.filter;

import com.wolfhouse.wolfhouseblog.auth.exceptions.AuthenticationJwtException;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.SecurityConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpConstant;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNullApi;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author linexsong
 */
@RequiredArgsConstructor
@Slf4j
@NonNullApi
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final ServiceAuthMediator mediator;
    private final AdminService adminService;
    @Resource(name = "authenticationEntryPoint")
    private AuthenticationEntryPoint entryPoint;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        String token = request.getHeader(HttpConstant.AUTH_HEADER);

        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            // 验证用户是否可达
            VerifyTool.of(UserVerifyNode.id(mediator)
                                        .target(userId))
                      .doVerify();

            log.info("JWT 信息: {}, 过期时间: {}", userId, claims.getExpiration());


            // 获取用户权限
            List<Authority> authorities = adminService.getAuthorities(userId);
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            // 保留验证信息
            SecurityContextHolder.getContext()
                                 .setAuthentication(auth);

            filterChain.doFilter(request, response);


        } catch (JwtException e) {
            // 该链接不强制 JWT 验证
            if (Arrays.stream(SecurityConstant.PUBLIC_URLS)
                      .toList()
                      .contains(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
            // 未认证
            entryPoint.commence(request,
                response,
                new AuthenticationJwtException(AuthExceptionConstant.BAD_TOKEN));
        } catch (Exception ignored) {
            filterChain.doFilter(request, response);
        }
    }
}

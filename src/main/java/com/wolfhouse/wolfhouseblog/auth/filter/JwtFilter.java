package com.wolfhouse.wolfhouseblog.auth.filter;

import com.wolfhouse.wolfhouseblog.auth.exceptions.AuthenticationJwtException;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.UserAuthException;
import com.wolfhouse.wolfhouseblog.common.http.HttpConstant;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.common.utils.UrlMatchUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.redis.RoleRedisService;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
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
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
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
    private final ServiceAuthMediator mediator;
    private final AdminService adminService;
    @Resource(name = "authenticationEntryPoint")
    private AuthenticationEntryPoint entryPoint;
    private final RoleRedisService roleRedisService;


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        // JWTFilter 执行了两次，第一次没有 Header，第二次才有
        UrlMatchUtil urlUtil = UrlMatchUtil.instance();

        String token = request.getHeader(HttpConstant.AUTH_HEADER);
        try {
            // token 为空，直接跳出
            if (BeanUtil.isBlank(token)) {
                throw new JwtException(null);
            }
            Long userId = roleRedisService.getAndRefreshToken(token);
            List<Authority> authorities;

            if (userId == null) {
                // 无缓存
                Claims claims = jwtUtil.parseToken(token);
                userId = Long.parseLong(claims.getSubject());
                // 验证用户是否可达
                VerifyTool.of(UserVerifyNode.id(mediator)
                                            .target(userId))
                          .doVerify();
                log.info("JWT 信息: {}, 过期时间: {}", userId, claims.getExpiration());
                // 保存缓存
                roleRedisService.saveTokenCache(token, userId.toString());
            }
            // 读取权限缓存
            authorities = roleRedisService.getAuthorities(userId);

            // 缓存中未读到权限，从数据库中读取
            if (authorities == null) {
                authorities = adminService.getAuthorities(userId);
                roleRedisService.saveAuthorities(userId, authorities);
            }

            // 保留验证信息
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext()
                                 .setAuthentication(auth);

            filterChain.doFilter(request, response);


        } catch (JwtException | IllegalArgumentException e) {
            // 该链接不强制 JWT 验证
            if (HttpMethod.OPTIONS.matches(request.getMethod()) || urlUtil.isPublic(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
            // 未认证
            entryPoint.commence(request,
                                response,
                                new AuthenticationJwtException(AuthExceptionConstant.BAD_TOKEN));
        } catch (UserAuthException e) {
            log.debug("用户登录失败，{}: 【{}】", e.getMessage(), e.getUserId());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            filterChain.doFilter(request, response);
        }
    }
}

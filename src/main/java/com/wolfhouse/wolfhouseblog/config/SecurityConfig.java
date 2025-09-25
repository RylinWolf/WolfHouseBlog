package com.wolfhouse.wolfhouseblog.config;

import com.wolfhouse.wolfhouseblog.auth.filter.JwtFilter;
import com.wolfhouse.wolfhouseblog.common.constant.SecurityConstant;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.redis.RoleRedisService;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author linexsong
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil,
                               ServiceAuthMediator mediator,
                               AdminService adminService,
                               RoleRedisService roleRedisService) {
        return new JwtFilter(jwtUtil, mediator, adminService, roleRedisService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationEntryPoint authenticationEntryPoint,
                                                   AccessDeniedHandler deniedHandler,
                                                   JwtFilter jwtFilter)
        throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            // 关闭 CSRF 保护
            .csrf(AbstractHttpConfigurer::disable)
            // 访问权限
            .authorizeHttpRequests(a -> {
                a.requestMatchers(SecurityConstant.STATIC_PATH_WHITELIST)
                 .permitAll();
                a.requestMatchers(SecurityConstant.PUBLIC_URLS)
                 .permitAll();
                a.requestMatchers("/webjars/**")
                 .permitAll();
                a.requestMatchers(HttpMethod.OPTIONS)
                 .permitAll();

                a.anyRequest()
                 .authenticated();
            })
            // 异常处理器
            .exceptionHandling((e) -> e.authenticationEntryPoint(authenticationEntryPoint)
                                       .accessDeniedHandler(deniedHandler))
            // 过滤器
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

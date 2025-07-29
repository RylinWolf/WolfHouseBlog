package com.wolfhouse.wolfhouseblog.config;

import co.elastic.clients.util.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.CharEncoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 请求异常时的处理器
 *
 * @author linexsong
 */
@Configuration
@RequiredArgsConstructor
public class EntryPointConfig {
    private final ObjectMapper defaultObjectMapper;

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType(ContentType.APPLICATION_JSON);
            response.setCharacterEncoding(CharEncoding.UTF_8);
            response.getWriter()
                    .write(defaultObjectMapper.writeValueAsString(
                            HttpResult.failed(
                                    HttpCodeConstant.UN_LOGIN,
                                    AuthExceptionConstant.UNAUTHORIZED)));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType(ContentType.APPLICATION_JSON);
            response.setCharacterEncoding(CharEncoding.UTF_8);

            var result = HttpResult.failed(
                    HttpCodeConstant.ACCESS_DENIED,
                    AuthExceptionConstant.ACCESS_DENIED);

            response.setStatus(HttpStatus.FORBIDDEN.value());

            response.getWriter()
                    .write(defaultObjectMapper.writeValueAsString(result));
        };
    }
}

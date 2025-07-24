package com.wolfhouse.wolfhouseblog.config;

import cn.hutool.json.JSONUtil;
import co.elastic.clients.util.ContentType;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import org.apache.commons.codec.CharEncoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 请求异常时的处理器
 *
 * @author linexsong
 */
@Configuration
public class EntryPointConfig {
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType(ContentType.APPLICATION_JSON);
            response.setCharacterEncoding(CharEncoding.UTF_8);
            response.getWriter()
                    .write(JSONUtil.toJsonStr(HttpResult.failed(
                            AuthExceptionConstant.UNAUTHORIZED,
                            HttpCodeConstant.UN_LOGIN)));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

        };
    }
}

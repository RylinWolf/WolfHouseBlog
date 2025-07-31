package com.wolfhouse.wolfhouseblog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhouse.wolfhouseblog.common.http.HttpMediaTypeConstant;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author linexsong
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource(name = "defaultObjectMapper")
    private ObjectMapper defaultObjectMapper;
    @Resource(name = "jsonNullableObjectMapper")
    private ObjectMapper jsonNullableObjectMapper;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:[*]")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 新增默认消息转换器
        var defaultConverter = new MappingJackson2HttpMessageConverter();
        defaultConverter.setObjectMapper(defaultObjectMapper);
        defaultConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
        converters.add(1, defaultConverter);

        // 新增 JsonNullable 消息转换器
        var jsonNullableConverter = new MappingJackson2HttpMessageConverter();
        jsonNullableConverter.setObjectMapper(jsonNullableObjectMapper);
        jsonNullableConverter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                HttpMediaTypeConstant.APPLICATION_JSON_NULLABLE));
        converters.add(1, jsonNullableConverter);
    }
}

package com.wolfhouse.wolfhouseblog.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wolfhouse.wolfhouseblog.common.properties.DateProperties;
import com.wolfhouse.wolfhouseblog.common.utils.JacksonObjectMapper;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;

/**
 * @author linexsong
 */
@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfig {
    private final DateProperties dateProperties;

    @Bean(name = "defaultObjectMapper")
    @Primary
    public ObjectMapper defaultObjectMapper() {
        return getDefault();
    }

    @Bean(name = "jsonNullableObjectMapper")
    public ObjectMapper jsonNullableObjectMapper() {
        var mapper = getDefault();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JsonNullableModule());
        return mapper;
    }

    @Bean(name = "esObjectMapper")
    public ObjectMapper esObjectMapper() {
        var mapper = getDefault();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JsonNullableModule());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }

    private ObjectMapper getDefault() {
        ObjectMapper mapper = new JacksonObjectMapper(dateProperties);
        // 配置日期格式
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat(dateProperties.datetime()));
        return mapper;
    }
}

package com.wolfhouse.wolfhouseblog.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wolfhouse.wolfhouseblog.common.properties.DateProperties;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author linexsong
 */
@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfig {
    private final DateProperties dateProperties;

    @Bean(name = "defaultObjectMapper")
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
    
    private ObjectMapper getDefault() {
        ObjectMapper mapper = new ObjectMapper();
        // 配置日期
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat(dateProperties.obj()));
        return mapper;
    }
}

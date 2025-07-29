package com.wolfhouse.wolfhouseblog.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.wolfhouse.wolfhouseblog.common.properties.DateProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author linexsong
 */
public class JacksonObjectMapper extends ObjectMapper {
    public JacksonObjectMapper(DateProperties properties) {
        // 配置未知属性不警告
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 配置时间转换
        var dateTime = DateTimeFormatter.ofPattern(properties.datetime());
        var date = DateTimeFormatter.ofPattern(properties.date());
        var time = DateTimeFormatter.ofPattern(properties.time());

        var simpleModule = new SimpleModule();

        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTime));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(date));
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(time));

        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTime));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(date));
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(time));

        this.registerModule(simpleModule);
    }

}

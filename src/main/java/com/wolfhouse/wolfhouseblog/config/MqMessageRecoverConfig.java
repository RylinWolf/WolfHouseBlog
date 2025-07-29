package com.wolfhouse.wolfhouseblog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhouse.wolfhouseblog.common.constant.mq.MqConstant;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linexsong
 */
@Configuration
@RequiredArgsConstructor
public class MqMessageRecoverConfig {
    private final RabbitTemplate template;
    private final ObjectMapper defaultObjectMapper;
    private final MessagePostProcessor messageIdPostProcessor;

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, MqConstant.ERROR_EXCHANGE);
    }

    @PostConstruct
    public void configureRabbitTemplate() {
        template.setMessageConverter(new Jackson2JsonMessageConverter(defaultObjectMapper));
        template.setBeforePublishPostProcessors(messageIdPostProcessor);
    }
}

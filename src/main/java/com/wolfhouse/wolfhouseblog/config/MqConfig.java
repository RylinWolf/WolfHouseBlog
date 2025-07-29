package com.wolfhouse.wolfhouseblog.config;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * @author linexsong
 */
@Configuration
public class MqConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        
        return new RabbitTemplate(factory);
    }

    @Bean
    public MessagePostProcessor messageIdPostProcessor() {
        return message -> {
            if (message.getMessageProperties()
                       .getMessageId() == null) {
                message.getMessageProperties()
                       .setMessageId(UUID.randomUUID()
                                         .toString());
            }
            return message;
        };
    }
}

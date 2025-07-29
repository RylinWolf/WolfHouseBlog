package com.wolfhouse.wolfhouseblog.mq.service;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqUserConstant;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class MqUserService {
    private final RabbitTemplate template;

    public void createUser(UserAuth auth) {
        template.convertAndSend(MqUserConstant.CREATE_EXCHANGE, MqUserConstant.KEY_CREATE_USER, auth);
    }

}

package com.wolfhouse.wolfhouseblog.mq.service;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqUserConstant;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqUserAuthDto;
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

    public void createUser(UserRegisterDto dto) {
        template.convertAndSend(MqUserConstant.CREATE_EXCHANGE, MqUserConstant.KEY_CREATE_USER, dto);
    }

    public void deleteUser(MqUserAuthDto dto) {
        template.convertAndSend(MqUserConstant.DELETE_EXCHANGE, MqUserConstant.KEY_DELETE_USER, dto);
    }

}

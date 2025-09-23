package com.wolfhouse.wolfhouseblog.mq.service;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqFavoritesConstant;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqAuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@RequiredArgsConstructor
@Component
public class MqFavoritesService {
    private final RabbitTemplate template;

    public void initDefaultFavorites(MqAuthDto dto) {
        template.convertAndSend(MqFavoritesConstant.INIT_EXCHANGE, MqFavoritesConstant.INIT_KEY, dto);
    }
}

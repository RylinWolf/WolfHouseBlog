package com.wolfhouse.wolfhouseblog.mq.service;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleConstant;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqPartitionChangeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class MqArticleService {
    private final RabbitTemplate template;

    public void articlePartitionChange(MqPartitionChangeDto dto) {
        template.convertAndSend(
             MqArticleConstant.PARTITION_CHANGE_EXCHANGE,
             MqArticleConstant.PARTITION_CHANGE_KEY,
             dto);
    }
}

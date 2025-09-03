package com.wolfhouse.wolfhouseblog.mq.service;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleConstant;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqArticleTagRemoveDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqFavoritesRemoveDto;
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
            MqArticleConstant.KEY_PARTITION_CHANGE,
            dto);
    }

    public void articleComUseTagsRemove(MqArticleTagRemoveDto dto) {
        template.convertAndSend(
            MqArticleConstant.TAG_REMOVE_EXCHANGE,
            MqArticleConstant.KEY_TAG_REMOVE,
            dto);
    }

    public void articleFavoritesRemove(MqFavoritesRemoveDto dto) {
        template.convertAndSend(
            MqArticleConstant.FAVORITES_REMOVE_EXCHANGE,
            MqArticleConstant.KEY_FAVORITES_REMOVE,
            dto);
    }
}

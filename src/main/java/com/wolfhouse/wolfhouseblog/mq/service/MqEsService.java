package com.wolfhouse.wolfhouseblog.mq.service;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleEsConstant;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class MqEsService {
    private final RabbitTemplate template;

    /**
     * 发布文章
     *
     * @param article 文章对象
     */
    public void postArticle(Article article) {
        template.convertAndSend(MqArticleEsConstant.POST_EXCHANGE, MqArticleEsConstant.POST_QUEUE, article);
    }
}

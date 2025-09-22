package com.wolfhouse.wolfhouseblog.mq.listener;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleEsConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class ArticleEsListener {
    private final ArticleElasticServiceImpl articleService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.POST_QUEUE),
        exchange = @Exchange(
            name = MqArticleEsConstant.POST_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqArticleEsConstant.POST_KEY}
    ))
    public void post(Article article) {
        articleService.saveOne(article);
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.UPDATE_QUEUE),
        exchange = @Exchange(
            name = MqArticleEsConstant.UPDATE_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqArticleEsConstant.UPDATE_KEY}
    ))
    public void update(ArticleUpdateDto dto) {
        try {
            articleService.update(dto);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.DELETE_QUEUE),
        exchange = @Exchange(
            name = MqArticleEsConstant.DELETE_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqArticleEsConstant.DELETE_KEY}
    ))
    public void delete(Long id) {
        try {
            articleService.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}

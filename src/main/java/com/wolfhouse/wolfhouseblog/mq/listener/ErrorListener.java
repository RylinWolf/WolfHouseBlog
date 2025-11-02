package com.wolfhouse.wolfhouseblog.mq.listener;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleConstant;
import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleEsConstant;
import com.wolfhouse.wolfhouseblog.common.constant.mq.MqConstant;
import com.wolfhouse.wolfhouseblog.common.constant.mq.MqUserConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Slf4j
@Component
public class ErrorListener {
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(
                value = MqConstant.ERROR_QUEUE,
                durable = "true",
                arguments = @Argument(name = MqConstant.LAZY_ARG, value = MqConstant.QUEUE_MODE_LAZY)),
            exchange = @Exchange(
                value = MqConstant.ERROR_EXCHANGE,
                type = ExchangeTypes.TOPIC),
            key = {MqConstant.ERROR + MqConstant.SEPARATOR + MqUserConstant.BASE + MqConstant.MULTI_WILDCARD}),
        concurrency = MqConstant.CONCURRENCY)
    public void userErrorListener(Object object) {
        log.error("监听到用户业务错误信息：【{}】", object);
    }


    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = MqConstant.ARTICLE_ERROR_QUEUE),
            exchange = @Exchange(
                name = MqConstant.ERROR_EXCHANGE,
                type = ExchangeTypes.TOPIC),
            key = {MqConstant.ERROR + MqConstant.SEPARATOR + MqArticleConstant.BASE + MqConstant.MULTI_WILDCARD}
        )
    )
    public void articleErrorListener(Object object) {
        log.error("监听到文章业务错误信息: 【{}】", object);
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqConstant.ES_ERROR_QUEUE),
        exchange = @Exchange(
            name = MqConstant.ERROR_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqConstant.ERROR + MqConstant.SEPARATOR + MqConstant.SERVICE + MqConstant.SEPARATOR +
               MqArticleEsConstant.ES + MqConstant.MULTI_WILDCARD}
    ))
    public void articleEsErrorListener(Object object) {
        log.error("监听到 ES 业务错误信息：【{}】", object);
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = MqConstant.FAVORITES_ERROR_QUEUE),
            exchange = @Exchange(
                name = MqConstant.ERROR_EXCHANGE,
                type = ExchangeTypes.TOPIC
            ),
            key = {MqConstant.ERROR + MqConstant.SEPARATOR + MqArticleConstant.BASE + MqConstant.MULTI_WILDCARD}
        )
    )
    public void favoritesErrorListener(Object object) {
        log.error("监听到收藏夹业务错误信息: 【{}】", object);
    }
}

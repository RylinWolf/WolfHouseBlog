package com.wolfhouse.wolfhouseblog.mq.listener;

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
    public void errorListener(Object object) {
        log.error("监听到错误信息：【{}】", object);

    }
}

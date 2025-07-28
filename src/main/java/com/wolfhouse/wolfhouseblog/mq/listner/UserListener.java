package com.wolfhouse.wolfhouseblog.mq.listner;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqUserConstant;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserAuth;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserListener {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqUserConstant.CREATE_QUEUE),
                    exchange = @Exchange(
                            name = MqUserConstant.CREATE_EXCHANGE,
                            type = ExchangeTypes.TOPIC
                    )
            )
    )
    public void userCreateListener(UserAuth auth) {
        log.info("{}", auth);

    }

}

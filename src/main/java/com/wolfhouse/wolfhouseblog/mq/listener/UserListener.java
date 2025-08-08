package com.wolfhouse.wolfhouseblog.mq.listener;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqUserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.mq.MqTools;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqUserAuthDto;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserListener {
    private final UserService userService;
    private final UserAuthService userAuthService;
    private final AdminService adminService;
    private final MqTools mqTools;

    @RabbitListener(
         bindings = @QueueBinding(
              value = @Queue(name = MqUserConstant.CREATE_QUEUE),
              exchange = @Exchange(
                   name = MqUserConstant.CREATE_EXCHANGE,
                   type = ExchangeTypes.TOPIC),
              key = {MqUserConstant.KEY_CREATE_USER}))
    public void userCreateListener(UserRegisterDto dto) {
        log.info("监听到用户创建：{}", dto);
    }


    @RabbitListener(
         bindings = @QueueBinding(
              value = @Queue(name = MqUserConstant.DELETE_QUEUE),
              exchange = @Exchange(
                   name = MqUserConstant.DELETE_EXCHANGE,
                   type = ExchangeTypes.TOPIC),
              key = {MqUserConstant.KEY_DELETE_USER}
         ))
    public void userDeleteListener(MqUserAuthDto dto) throws Exception {
        log.info("监听到用户删除: {}", dto.getUserId());
        mqTools.setLoginAuth(dto);

        try {
            userService.deleteAccount(dto.getUserId());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}

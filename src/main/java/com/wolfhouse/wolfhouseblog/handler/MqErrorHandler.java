package com.wolfhouse.wolfhouseblog.handler;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * @author linexsong
 */
@Slf4j
@Component
public class MqErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable t) {
        log.error(
                """
                [{}]: {}
                """, MqConstant.EXCEPTION_SERVICE, t.getMessage());
    }
}

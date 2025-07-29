package com.wolfhouse.wolfhouseblog.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author linexsong
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public void handleException(Exception e) {
        log.error("发生异常: [{}]", e.getMessage(), e);
    }
}

package com.wolfhouse.wolfhouseblog.handler;

import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author linexsong
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleException(Exception e) {
        log.error("发生异常: [{}]", e.getMessage(), e);
        return HttpResult.failed(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpCodeConstant.SERVER_ERROR,
                ServiceExceptionConstant.SERVER_ERROR);
    }
}

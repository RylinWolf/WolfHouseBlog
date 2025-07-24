package com.wolfhouse.wolfhouseblog.handler;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author linexsong
 */
@ControllerAdvice
public class HttpExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(HttpResult.failed(AuthExceptionConstant.ACCESS_DENIED, HttpCodeConstant.METHOD_NOT_ALLOWED));
    }

}

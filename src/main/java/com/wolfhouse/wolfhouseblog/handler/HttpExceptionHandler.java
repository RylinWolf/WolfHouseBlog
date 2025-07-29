package com.wolfhouse.wolfhouseblog.handler;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author linexsong
 */
@ControllerAdvice(
        basePackages = {
                "com.wolfhouse.wolfhouseblog.controller",
                "com.wolfhouse.wolfhouseblog.service"})
@Slf4j
public class HttpExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(HttpResult.failed(HttpCodeConstant.METHOD_NOT_ALLOWED, AuthExceptionConstant.ACCESS_DENIED));
    }

    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleMethodHttpMsgNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(HttpResult.failed(HttpCodeConstant.BAD_REQUEST, AuthExceptionConstant.BAD_REQUEST));
    }

    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(HttpResult.failed(
                                     HttpCodeConstant.ARG_NOT_VALID,
                                     ServiceExceptionConstant.ARG_FORMAT_ERROR));
    }

}

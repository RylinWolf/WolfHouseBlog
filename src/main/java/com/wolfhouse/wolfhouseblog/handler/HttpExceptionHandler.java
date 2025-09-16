package com.wolfhouse.wolfhouseblog.handler;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author linexsong
 */
@Hidden
@ControllerAdvice
@Order(1)
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
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(HttpResult.failed(HttpCodeConstant.BAD_REQUEST, AuthExceptionConstant.BAD_REQUEST));
    }

    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        FieldError field = e.getFieldError();
        String fieldName = "";
        String message = "";
        if (field != null) {
            fieldName = field.getField();
            message = field.getDefaultMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(HttpResult.failed(
                                 HttpCodeConstant.ARG_NOT_VALID,
                                 fieldName + ServiceExceptionConstant.ARG_FORMAT_ERROR + ":" + message));
    }

    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(e.getMessage());
        return HttpResult.failed(
            HttpStatus.NOT_FOUND.value(),
            HttpCodeConstant.NOT_FOUND,
            ServiceExceptionConstant.NO_RESOURCE);
    }

}

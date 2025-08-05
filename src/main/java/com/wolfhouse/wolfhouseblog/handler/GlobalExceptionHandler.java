package com.wolfhouse.wolfhouseblog.handler;

import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
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

    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleException(VerifyException e) {
        log.error("字段验证异常: [{}]", e.getMessage(), e);
        return HttpResult.failed(
                HttpStatus.FORBIDDEN.value(),
                HttpCodeConstant.VERIFY_FAILED,
                VerifyConstant.VERIFY_FAILED);
    }

    @ExceptionHandler
    public ResponseEntity<HttpResult<?>> handleException(ServiceException e) {
        String message = e.getMessage();
        log.error("服务异常: [{}]", message);
        String code;
        int status;
        switch (message) {
            case ServiceExceptionConstant.LOGIN_REQUIRED -> {
                code = HttpCodeConstant.UN_LOGIN;
                status = HttpStatus.UNAUTHORIZED.value();
            }
            default -> {
                code = HttpCodeConstant.SERVER_ERROR;
                status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
        }

        return HttpResult.failed(status, code, ServiceExceptionConstant.SERVER_ERROR);
    }
}

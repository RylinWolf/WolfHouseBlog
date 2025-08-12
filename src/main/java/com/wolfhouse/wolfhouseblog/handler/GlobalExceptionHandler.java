package com.wolfhouse.wolfhouseblog.handler;

import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyException;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
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
        log.error("字段验证异常: [{}]", e.getMessage());
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
        var msg = message;
        int status;

        switch (message) {
            case AuthExceptionConstant.LOGIN_REQUIRED -> {
                code = HttpCodeConstant.UN_LOGIN;
                status = HttpStatus.UNAUTHORIZED.value();
            }
            case AuthExceptionConstant.ACCESS_DENIED,
                 AuthExceptionConstant.AUTHENTIC_FAILED,
                 AdminConstant.ADMIN_EXISTS,
                 AdminConstant.ADMIN_NOT_EXIST,
                 VerifyConstant.NOT_ALL_BLANK,
                 PartitionConstant.NOT_EXIST,
                 PartitionConstant.ALREADY_EXIST -> {
                code = HttpCodeConstant.ACCESS_DENIED;
                status = HttpStatus.FORBIDDEN.value();
            }
            case VerifyConstant.VERIFY_FAILED -> {
                code = HttpCodeConstant.VERIFY_FAILED;
                status = HttpStatus.FORBIDDEN.value();
            }
            case UserConstant.USER_HAS_BEEN_BANNED -> {
                code = HttpCodeConstant.BANNED;
                status = HttpStatus.FORBIDDEN.value();
            }
            case UserConstant.USER_NOT_EXIST, UserConstant.USER_UNACCESSIBLE -> {
                code = HttpCodeConstant.USER_NOT_EXIST;
                status = HttpStatus.FORBIDDEN.value();
            }
            case UserConstant.SUBSCRIBE_FAILED -> {
                code = HttpCodeConstant.FAILED;
                status = HttpStatus.FORBIDDEN.value();
            }
            default -> {
                code = HttpCodeConstant.SERVICE_ERROR;
                status = HttpStatus.INTERNAL_SERVER_ERROR.value();
                msg = ServiceExceptionConstant.SERVICE_ERROR + ": " + message;
            }
        }

        return HttpResult.failed(status, code, msg);
    }
}

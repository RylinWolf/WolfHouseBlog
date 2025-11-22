package com.wolfhouse.wolfhouseblog.common.utils.result;

import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * @author Rylin Wolf
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HttpResult<T> implements Serializable {
    private Boolean success;
    private String message;
    private Integer code;
    private T data;

    public static <T> HttpResult<T> success() {
        return HttpResult.success(null);
    }

    public static <T> HttpResult<T> success(T data) {
        return HttpResult.success(data, null);
    }

    public static <T> HttpResult<T> success(T data, String msg) {
        return HttpResult.<T>builder()
                         .code(HttpCode.SUCCESS.code)
                         .success(true)
                         .data(data)
                         .message(msg)
                         .build();
    }

    public static <T> HttpResult<T> failed(@NonNull HttpCode code, String msg, T data) {
        return failed(code.code, msg, data);
    }

    public static <T> HttpResult<T> failed(@NonNull Integer code, String msg, T data) {
        return HttpResult.<T>builder()
                         .success(false)
                         .code(code)
                         .message(msg)
                         .data(data)
                         .build();
    }

    public static <T> HttpResult<T> failed(@NonNull HttpCode code, T data) {
        return failed(code, code.message, data);
    }

    public static <T> HttpResult<T> failed(HttpCode code) {
        return failed(code, null);
    }

    public static <T> HttpResult<T> failed() {
        return failed(HttpCode.UNKNOWN, null);
    }


    public static <T> HttpResult<T> failedIfBlank(HttpCode code, String failedMsg, T data) {
        return BeanUtil.isBlank(data) ? HttpResult.failed(code, failedMsg, data) : HttpResult.success(data);
    }

    public static <T> HttpResult<T> failedIfBlank(HttpCode code, T data) {
        return BeanUtil.isBlank(data) ? HttpResult.failed(code, code.message, data) : HttpResult.success(data);
    }

    public static <T> HttpResult<T> failedIfBlank(T data) {
        return failedIfBlank(null, null, data);
    }

    public static HttpResult<?> onCondition(HttpCode code, String msg, Boolean condition) {
        return condition ? HttpResult.success() : HttpResult.failed(code, msg, null);
    }

    public static HttpResult<?> onCondition(HttpCode code, Boolean condition) {
        return condition ? HttpResult.success() : HttpResult.failed(code);
    }

    public static HttpResult<?> onCondition(Boolean condition) {
        return condition ? HttpResult.success() : HttpResult.failed();
    }

    public static <T> ResponseEntity<HttpResult<T>> failedWithStatus(Integer httpStatus,
                                                                     HttpCode code,
                                                                     String msg,
                                                                     T data) {
        return ResponseEntity.status(httpStatus)
                             .body(HttpResult.failed(code, msg, data));
    }

    public static ResponseEntity<HttpResult<?>> failedWithStatus(Integer httpStatus, HttpCode code, String msg) {
        return ResponseEntity.status(httpStatus)
                             .body(HttpResult.failed(code, msg, null));
    }

    public static ResponseEntity<HttpResult<?>> failedWithStatus(Integer httpStatus, HttpCode code) {
        return ResponseEntity.status(httpStatus)
                             .body(HttpResult.failed(code));
    }


    public static ResponseEntity<HttpResult<?>> failedWithStatus(HttpCode code, String msg) {
        return failedWithStatus(Integer.parseInt(String.valueOf(code.code)
                                                       .substring(0, 3)), code, msg);
    }

    public static ResponseEntity<HttpResult<?>> failedWithStatus(HttpCode code) {
        return failedWithStatus(code, null);
    }

    public static <T> ResponseEntity<HttpResult<T>> failedIfBlank(Integer httpStatus,
                                                                  HttpCode code,
                                                                  String message,
                                                                  T data) {
        HttpResult<T> res = HttpResult.failedIfBlank(code, message, data);

        return ResponseEntity.status(res.success ? HttpStatus.OK.value() : httpStatus)
                             .body(res);
    }

    public static <T> ResponseEntity<HttpResult<T>> ok(T data, String msg) {
        return ResponseEntity.ok(HttpResult.success(data, msg));
    }

    public static <T> ResponseEntity<HttpResult<T>> ok(T data) {
        return ResponseEntity.ok(HttpResult.success(data));
    }


}

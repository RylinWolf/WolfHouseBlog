package com.wolfhouse.wolfhouseblog.common.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * @author linexsong
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HttpResult<T> implements Serializable {
    private Boolean success;
    private String message;
    private String code;
    private T data;

    public static <T> HttpResult<T> success() {
        return HttpResult.success(null);
    }

    public static <T> HttpResult<T> success(T data) {
        return HttpResult.success(data, null);
    }

    public static <T> HttpResult<T> success(T data, String msg) {
        return HttpResult.<T>builder()
                         .code(HttpCodeConstant.SUCCESS)
                         .success(true)
                         .data(data)
                         .message(msg)
                         .build();
    }

    public static <T> HttpResult<T> failed() {
        return HttpResult.failed(null);
    }

    public static <T> HttpResult<T> failed(String msg) {
        return HttpResult.failed(HttpCodeConstant.FAILED, msg);
    }

    public static <T> HttpResult<T> failed(String code, String msg) {
        return HttpResult.failed(code, msg, null);
    }

    public static <T> HttpResult<T> failed(String code, String msg, T data) {
        return HttpResult.<T>builder()
                         .success(false)
                         .code(code)
                         .message(msg)
                         .data(data)
                         .build();
    }

    public static <T> ResponseEntity<HttpResult<T>> failed(Integer httpStatus, String code, String msg, T data) {
        return ResponseEntity.status(httpStatus)
                             .body(HttpResult.failed(code, msg, data));
    }

    public static <T> ResponseEntity<HttpResult<T>> ok(T data, String msg) {
        return ResponseEntity.ok(HttpResult.success(data, msg));
    }
}

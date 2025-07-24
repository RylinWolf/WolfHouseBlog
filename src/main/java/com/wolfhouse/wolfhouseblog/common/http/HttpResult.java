package com.wolfhouse.wolfhouseblog.common.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public static HttpResult<?> failed() {
        return HttpResult.failed(null);
    }

    public static HttpResult<?> failed(String msg) {
        return HttpResult.failed(HttpCodeConstant.FAILED, msg);
    }

    public static HttpResult<?> failed(String code, String msg) {
        return HttpResult.builder()
                         .success(false)
                         .code(code)
                         .message(msg)
                         .build();
    }
}

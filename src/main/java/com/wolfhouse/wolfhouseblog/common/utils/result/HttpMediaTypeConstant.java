package com.wolfhouse.wolfhouseblog.common.utils.result;

import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author linexsong
 */
public class HttpMediaTypeConstant extends MediaType {

    public static final MediaType APPLICATION_JSON_PATCH = new MediaType(
        "application",
        "json-patch+json",
        StandardCharsets.UTF_8);
    public static final String APPLICATION_JSON_PATCH_VALUE = "application/json-patch+json";

    public HttpMediaTypeConstant(String type) {
        super(type);
    }

    public HttpMediaTypeConstant(String type, String subtype) {
        super(type, subtype);
    }

    public HttpMediaTypeConstant(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    public HttpMediaTypeConstant(String type, String subtype, double qualityValue) {
        super(type, subtype, qualityValue);
    }

    public HttpMediaTypeConstant(MediaType other, Charset charset) {
        super(other, charset);
    }

    public HttpMediaTypeConstant(MediaType other, Map<String, String> parameters) {
        super(other, parameters);
    }

    public HttpMediaTypeConstant(String type, String subtype, Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

    public HttpMediaTypeConstant(MimeType mimeType) {
        super(mimeType);
    }
}

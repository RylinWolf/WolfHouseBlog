package com.wolfhouse.wolfhouseblog.common.http;

import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author linexsong
 */
public class HttpMediaTypeConstant extends MediaType {

    public static final MediaType APPLICATION_JSON_NULLABLE = new MediaType(
            "application",
            "jsonNullable",
            StandardCharsets.UTF_8);

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

package com.wolfhouse.wolfhouseblog.common.utils;

import org.openapitools.jackson.nullable.JsonNullable;

/**
 * @author linexsong
 */
public class JsonNullableUtil {
    public static <T> T getObjOrNull(JsonNullable<T> nullable) {
        return nullable.isPresent() ? nullable.get() : null;
    }
}

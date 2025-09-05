package com.wolfhouse.wolfhouseblog.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linexsong
 */
@ConfigurationProperties(prefix = "custom.file-upload")
public record FileUploadProperties(String basepath, String avatar) {
}

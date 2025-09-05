package com.wolfhouse.wolfhouseblog.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linexsong
 */
@ConfigurationProperties(prefix = "custom.oss")
public record OssProperties(String endpoint, String bucket) {}

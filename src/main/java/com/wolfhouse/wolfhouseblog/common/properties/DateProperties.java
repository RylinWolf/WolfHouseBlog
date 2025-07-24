package com.wolfhouse.wolfhouseblog.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linexsong
 */
@ConfigurationProperties(prefix = "custom.date")
public record DateProperties(String obj) {
}

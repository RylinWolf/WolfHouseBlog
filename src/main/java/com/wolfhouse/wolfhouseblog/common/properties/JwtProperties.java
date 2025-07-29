package com.wolfhouse.wolfhouseblog.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linexsong
 */
@ConfigurationProperties(prefix = "custom.jwt")
public record JwtProperties(String secret, Long expiration) {
}

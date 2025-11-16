package com.wolfhouse.wolfhouseblog.config.oss;

import com.wolfhouse.wolfhouseblog.common.utils.oss.OssUtilClient;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rylin Wolf
 */
@Configuration
@ConfigurationProperties(prefix = "custom.oss.aliyun")
@ConditionalOnProperty(prefix = "custom.oss.aliyun", name = {"endpoint", "region"})
@Data
public class OssClientConfig {
    private String endpoint;
    private String region;
    private String avatarBucketName;
    private String customEndpoint;

    private String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
    private String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");

    @Bean(destroyMethod = "shutdown")
    public OssUtilClient ossClient() {
        return new OssUtilClient(accessKeyId, accessKeySecret, region, endpoint);
    }

}

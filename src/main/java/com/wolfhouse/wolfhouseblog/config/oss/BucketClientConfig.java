package com.wolfhouse.wolfhouseblog.config.oss;

import cn.hutool.core.util.StrUtil;
import com.wolfhouse.wolfhouseblog.common.utils.oss.BucketClient;
import com.wolfhouse.wolfhouseblog.common.utils.oss.OssUtilClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rylin Wolf
 */
@Configuration
@RequiredArgsConstructor
public class BucketClientConfig {
    private final OssUtilClient client;
    private final OssClientConfig config;

    @Bean
    public BucketClient avatarBucket() {
        BucketClient bucket = client.getBucket(config.getAvatarBucketName());
        bucket.setDirPrefix("avatar");
        String customEndpoint = config.getCustomEndpoint();
        if (StrUtil.isNotBlank(customEndpoint)) {
            bucket.setCustomEndpoint(customEndpoint);
        }
        return bucket;
    }
}

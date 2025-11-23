package com.wolfhouse.wolfhouseblog.config.redisutil;

import com.wolfhouse.wolfhouseblog.common.utils.redisutil.RedisKeyUtil;
import com.wolfhouse.wolfhouseblog.redis.ServiceRedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Rylin Wolf
 */
@Configuration
public class ServiceRedisUtilConfig {
    @Bean
    public ServiceRedisUtil serviceRedisUtil(RedisTemplate<String, Object> template, RedisKeyUtil keyUtil) {
        return new ServiceRedisUtil(template, keyUtil);
    }
}

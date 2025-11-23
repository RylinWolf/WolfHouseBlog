package com.wolfhouse.wolfhouseblog.config.redisutil;

import com.wolfhouse.wolfhouseblog.common.utils.redisutil.RedisKeyUtil;
import com.wolfhouse.wolfhouseblog.common.utils.redisutil.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Rylin Wolf
 */
@Configuration
public class RedisUtilConfig {
    @Bean
    public RedisUtil redisUtil(RedisTemplate<String, Object> template) {
        return new RedisUtil(template);
    }

    @Bean
    public RedisKeyUtil redisKeyUtil() {
        return new RedisKeyUtil(null, null);
    }
}

package com.wolfhouse.wolfhouseblog.common.constant;

import com.wolfhouse.wolfhouseblog.common.utils.redisutil.RedisKey;
import org.springframework.stereotype.Component;

/**
 * @author Rylin Wolf
 */
@RedisKey(isKeysConstant = true)
@Component
public class OssRedisConstant {
    public static final String OSS_DELETE_SET = "fileDelete";
}

package com.wolfhouse.wolfhouseblog.service.mediator.impl;

import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import com.wolfhouse.wolfhouseblog.redis.UserRedisService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import com.wolfhouse.wolfhouseblog.service.mediator.UserEsDbMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class UserEsDbMediatorImpl implements UserEsDbMediator {
    private final UserRedisService redisService;
    private final UserService userService;

    @Override
    public UserVo getUserVoById(Long id) throws Exception {
        // 从缓存中获取
        if (redisService.existsUserCache(id)) {
            return redisService.getUserInfo(id);
        }
        // 无缓存，从数据库获取并缓存
        UserVo userVo = userService.getUserVoById(id);
        redisService.userInfoCache(userVo);

        return userVo;
    }
}

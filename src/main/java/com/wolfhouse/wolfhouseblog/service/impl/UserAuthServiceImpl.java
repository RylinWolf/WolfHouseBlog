package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.mapper.UserAuthMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserAuth;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {
    private final PasswordEncoder encoder;
    // TODO 实现用户验证服务

    @Override
    public UserAuth createAuth(String password) {
        var auth = UserAuth.builder()
                           .password(encoder.encode(password))
                           .build();
        if (mapper.insertOne(auth) > 0) {
            return auth;
        }
        throw ServiceException.processingFailed(UserConstant.USER_AUTH_CREATE_FAILED);
    }

    @Override
    public Boolean createAuth(UserAuth userAuth) {
        mapper.insert(userAuth);
        return null;
    }

    @Override
    public Boolean updateAuth(UserAuth userAuth) {
        return null;
    }

    @Override
    public Boolean deleteAuth(Long userId) {
        return null;
    }

    @Override
    public Boolean verifyPassword(String password, Long userId) {
        return encoder.matches(
                password, mapper.selectOneById(userId)
                                .getPassword());
    }
}

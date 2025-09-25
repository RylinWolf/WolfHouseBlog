package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserIdVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.UserAuthMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.UserAuth;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.UserAuthTableDef.USER_AUTH;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {
    private final PasswordEncoder encoder;
    private final ServiceAuthMediator mediator;

    @PostConstruct
    private void init() {
        this.mediator.registerUserAuth(this);
    }

    @Override
    public Boolean isAuthExist(Long userId) {
        if (userId == null) {
            return false;
        }

        return selectWithoutLogicDelete(() -> exists(QueryWrapper.create()
                                                                 .where(USER_AUTH.USER_ID.eq(userId))));
    }

    @Override
    public Long loginUserOrE() throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        VerifyNode<Long> node = new UserIdVerifyNode(mediator).target(login);
        if (node.verify()) {
            return login;
        }
        ServiceUtil.removeLogin();
        throw node.getException();
    }

    /**
     * 若指定用户验证 ID 不存在，则抛出异常
     *
     * @param userId 用户 ID
     */
    private void throwIfNotExist(Long userId) {
        if (!isAuthExist(userId)) {
            throw new ServiceException(UserConstant.USER_NOT_EXIST);
        }
    }

    private static <T> T selectWithoutLogicDelete(Supplier<T> supplier) {
        return LogicDeleteManager.execWithoutLogicDelete(supplier);
    }

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
        userAuth.setUserId(null);
        return mapper.insert(userAuth) == 1;
    }

    @Override
    public Boolean enableAuth(Long userId) {
        throwIfNotExist(userId);
        return UpdateChain.of(UserAuth.class)
                          .where(USER_AUTH.USER_ID.eq(userId))
                          .set(USER_AUTH.IS_ENABLED, true)
                          .update();
    }

    @Override
    public void disableAuth(Long userId) {
        throwIfNotExist(userId);
        UpdateChain.of(UserAuth.class)
                   .where(USER_AUTH.USER_ID.eq(userId))
                   .set(USER_AUTH.IS_ENABLED, false)
                   .update();
    }

    @Override
    public Boolean deleteAuth(Long userId) {
        return !isUserDeleted(userId) && mapper.deleteById(userId) > 0;
    }

    @Override
    public Boolean isUserDeleted(Long userId) {
        throwIfNotExist(userId);
        return selectWithoutLogicDelete(
            () -> mapper
                .selectOneByQuery(
                    QueryWrapper.create()
                                .select(USER_AUTH.IS_DELETED)
                                .where(USER_AUTH.USER_ID.eq(userId)))
                .getIsDeleted());
    }

    @Override
    public Boolean isUserEnabled(Long userId) {
        throwIfNotExist(userId);
        return selectWithoutLogicDelete(
            () -> mapper
                .selectOneByQuery(
                    QueryWrapper
                        .create()
                        .select(USER_AUTH.IS_ENABLED)
                        .where(USER_AUTH.USER_ID.eq(userId)))
                .getIsEnabled());
    }

    @Override
    public Boolean isUserUnaccessible(Long userId) {
        return !isAuthExist(userId) || isUserDeleted(userId) || !isUserEnabled(userId);
    }

    @Override
    public Boolean verifyPassword(String password, Long userId) {
        throwIfNotExist(userId);

        return encoder.matches(
            password,
            mapper.selectOneById(userId)
                  .getPassword());
    }
}

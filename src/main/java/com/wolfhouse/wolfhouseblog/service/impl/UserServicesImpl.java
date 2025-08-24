package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.NotEqualsVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.SubscribeMapper;
import com.wolfhouse.wolfhouseblog.mapper.UserMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Subscribe;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserSubDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserRegisterVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.SubscribeTableDef.SUBSCRIBE;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.UserTableDef.USER;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class UserServicesImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final SubscribeMapper subscribeMapper;
    private final ServiceAuthMediator mediator;
    private final UserAuthService authService;
    private final JwtUtil jwtUtil;

    @PostConstruct
    private void init() {
        this.mediator.registerUser(this);
    }

    @Override
    public User findByAccountOrEmail(String s) {
        QueryWrapper wrap = new QueryWrapper();
        wrap.eq(User::getAccount, s)
            .or(wrapper -> {
                wrapper.eq(User::getEmail, s);
            });
        Optional<User> user = Optional.ofNullable(this.mapper.selectOneByQuery(wrap));
        return user.orElse(null);

    }

    @Override
    public User findByUserId(Long userId) throws Exception {
        Optional<User> user = Optional.ofNullable(this.mapper.selectOneById(userId));
        return user.orElse(null);
    }

    @Override
    public UserRegisterVo createUser(UserRegisterDto dto) throws Exception {
        int insert = mapper.insert(
             User.builder()
                 .id(dto.getUserId())
                 .email(dto.getEmail())
                 .username(dto.getUsername())
                 // 随机生成账号
                 .account(generateAccount(
                      dto.getUsername(),
                      UserConstant.DEFAULT_ACCOUNT_CODE_LEN))
                 .build(), true);
        // 插入不成功
        if (insert <= 0) {
            return null;
        }

        UserRegisterVo vo = BeanUtil.copyProperties(getUserVoById(dto.getUserId()), UserRegisterVo.class);
        vo.setToken(jwtUtil.getToken(String.valueOf(vo.getId())));
        return vo;
    }

    @Override
    public UserVo updateAuthedUser(UserUpdateDto dto) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        // 验证 DTO
        VerifyTool.of(
                       UserVerifyNode.id(mediator)
                                     .target(login),
                       UserVerifyNode.BIRTH.target(dto.getBirth()),
                       UserVerifyNode.email(this)
                                     .target(dto.getEmail())
                                     .allowNull(false))
                  .doVerify();

        User user = BeanUtil.copyProperties(dto, User.class);
        user.setId(login);

        if (mapper.update(user) != 1) {
            throw new ServiceException(UserConstant.USER_UPDATE_FAILED);
        }

        return BeanUtil.copyProperties(getUserVoById(user.getId()), UserVo.class);
    }

    @Override
    public String generateAccount(String username, Integer codeLen) {
        int countCode = new Random().nextInt((int) Math.pow(10, codeLen - 1), (int) Math.pow(10, codeLen));
        String account = username + UserConstant.ACCOUNT_SEPARATOR;
        account += String.format("%0" + codeLen + "d", countCode);

        // 生成账号重复，重新生成
        // TODO 使用 Redis 优化
        if (hasAccountOrEmail(account)) {
            return generateAccount(username, codeLen);
        }
        return account;
    }

    @Override
    public UserVo getUserVoById(Long id) throws Exception {
        // 检查 ID 是否可达
        VerifyTool.of(UserVerifyNode.id(mediator)
                                    .target(id))
                  .doVerify();

        return BeanUtil.copyProperties(mapper.selectOneById(id), UserVo.class);
    }

    @Override
    public List<UserVo> getUserVosByName(String name) throws Exception {
        // 验证登录信息与用户名
        VerifyTool.ofLoginExist(
                       mediator,
                       UserVerifyNode.USERNAME.target(name))
                  .doVerify();

        return BeanUtil.copyList(
             mapper.selectListByQuery(
                  QueryWrapper.create()
                              .where(USER.USERNAME.like(name))), UserVo.class);
    }

    @Override
    public Boolean hasAccountOrEmail(String s) {
        long count = mapper.selectCountByQuery(
             new QueryWrapper().eq(User::getAccount, s)
                               .or((Consumer<QueryWrapper>) w -> w.eq(User::getEmail, s)));
        return count > 0;

    }

    @Override
    public Boolean subscribe(UserSubDto dto) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        Long toUser = dto.getToUser();
        VerifyTool.ofLoginExist(
                       mediator,
                       new NotEqualsVerifyNode<>(login, toUser).exception(new ServiceException(UserConstant.SUBSCRIBE_FAILED)))
                  .doVerify();

        if (mediator.isUserUnaccessible(toUser)) {
            throw new ServiceException(UserConstant.USER_UNACCESSIBLE);
        }

        dto.setFromUser(login);
        if (isSubscribed(dto)) {
            throw new ServiceException(UserConstant.USER_ALREADY_SUBSCRIBED);
        }

        return subscribeMapper.insert(BeanUtil.copyProperties(dto, Subscribe.class)) == 1;
    }

    @Override
    public PageResult<UserBriefVo> getSubscribedUsers(UserSubDto dto) throws Exception {
        Long userId = dto.getFromUser();
        VerifyTool.ofLoginExist(
                       mediator,
                       UserVerifyNode.id(mediator)
                                     .target(userId))
                  .doVerify();

        // select * from user where user.id in (select to_user from sub where from_user = #{})
        QueryWrapper subsWrapper = QueryWrapper.create()
                                               .select(SUBSCRIBE.TO_USER)
                                               .where(SUBSCRIBE.FROM_USER.eq(userId))
                                               .from(SUBSCRIBE);
        QueryWrapper getBriefWrapper = QueryWrapper.create()
                                                   .select(UserBriefVo.COLUMNS)
                                                   .from(USER)
                                                   .where(USER.ID.in(subsWrapper));
        return PageResult.of(
             mapper.paginate(dto.getPageNumber(), dto.getPageSize(), getBriefWrapper),
             UserBriefVo.class);
    }

    @Override
    public Boolean isSubscribed(UserSubDto dto) {
        if (BeanUtil.isAnyBlank(dto.getFromUser(), dto.getToUser())) {
            throw new ServiceException(ServiceExceptionConstant.ARG_FORMAT_ERROR);
        }

        // 关注用户为自己
        if (dto.getToUser()
               .equals(dto.getFromUser())) {
            throw new ServiceException(UserConstant.SUBSCRIBE_CANNOT_BE_SELF);
        }

        return subscribeMapper.selectCountByQuery(QueryWrapper.create()
                                                              .where(SUBSCRIBE.FROM_USER.eq(dto.getFromUser()))
                                                              .and(SUBSCRIBE.TO_USER.eq(dto.getToUser()))) != 0;
    }

    @Override
    public Boolean deleteAccount(Long userId) throws Exception {
        VerifyTool.ofLoginExist(
                       mediator,
                       UserVerifyNode.id(mediator)
                                     .target(userId))
                  .doVerify();
        return authService.deleteAuth(userId);
    }

    @Override
    public void disableAccount(Long userId) throws Exception {
        VerifyTool.ofLoginExist(
                       mediator,
                       UserVerifyNode.id(mediator)
                                     .target(userId)
                               )
                  .doVerify();
        authService.disableAuth(userId);
    }

    @Override
    public Boolean unsubscribe(UserSubDto dto) throws Exception {
        Long login = authService.loginUserOrE();
        dto.setFromUser(login);

        if (!isSubscribed(dto)) {
            throw new ServiceException(UserConstant.NOT_SUBSCRIBED);
        }
        int count = subscribeMapper.deleteByQuery(QueryWrapper.create()
                                                              .eq(Subscribe::getFromUser, login)
                                                              .eq(Subscribe::getToUser, dto.getToUser()));
        if (count != 1) {
            throw new ServiceException(UserConstant.UNSUBSCRIBE_FAILED);
        }
        return true;
    }
}

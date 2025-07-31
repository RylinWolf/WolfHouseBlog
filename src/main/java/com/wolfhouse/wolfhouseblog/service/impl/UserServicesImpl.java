package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.UserMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserRegisterVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import com.wolfhouse.wolfhouseblog.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

/**
 * @author linexsong
 */
@Service
public class UserServicesImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public Optional<User> findByAccountOrEmail(String s) {
        QueryWrapper wrap = new QueryWrapper();
        wrap.eq(User::getAccount, s)
            .or(wrapper -> {
                wrapper.eq(User::getEmail, s);
            });
        return Optional.ofNullable(this.mapper.selectOneByQuery(wrap));
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return Optional.ofNullable(this.mapper.selectOneById(userId));
    }

    @Override
    public UserRegisterVo createUser(UserRegisterDto dto) {
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

        return BeanUtil.copyProperties(getUserVoById(dto.getUserId()), UserRegisterVo.class);
    }

    @Override
    public UserVo updateAuthedUser(UserDto dto) throws Exception {
        // 验证 DTO
        VerifyTool.ofAllMsg(
                          UserConstant.USER_UPDATE_FAILED,
                          UserVerifyNode.BIRTH.target(dto.getBirth()),
                          UserVerifyNode.email(this)
                                        .target(dto.getEmail()))
                  .doVerify();

        User user = BeanUtil.copyProperties(dto, User.class);
        user.setId(ServiceUtil.loginUser());

        if (mapper.update(user) != 1) {
            throw new ServiceException(UserConstant.USER_UPDATE_FAILED);
        }

        return BeanUtil.copyProperties(getUserVoById(user.getId()), UserVo.class);
    }

    @Override
    public String generateAccount(String username, Integer codeLen) {
        String account = username + UserConstant.ACCOUNT_SEPARATOR
                         + new Random().nextInt((int) Math.pow(10, codeLen - 1), (int) Math.pow(10, codeLen));

        account = String.format("%0" + codeLen + "d", account);

        // 生成账号重复，重新生成
        if (hasAccountOrEmail(account)) {
            return generateAccount(username, codeLen);
        }
        return account;
    }

    @Override
    public UserVo getUserVoById(Long id) {
        return BeanUtil.copyProperties(mapper.selectOneById(id), UserVo.class);
    }

    @Override
    public Boolean hasAccountOrEmail(String s) {
        long count = mapper.selectCountByQuery(
                new QueryWrapper().eq(User::getAccount, s)
                                  .or((Consumer<QueryWrapper>) w -> w.eq(User::getEmail, s)));
        return count > 0;

    }
}

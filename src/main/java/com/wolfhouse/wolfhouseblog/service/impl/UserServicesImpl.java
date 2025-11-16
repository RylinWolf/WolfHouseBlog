package com.wolfhouse.wolfhouseblog.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.aliyun.oss.model.CannedAccessControlList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.OssUploadLogConstant;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.imageutil.ImgCompressor;
import com.wolfhouse.wolfhouseblog.common.utils.imageutil.ImgValidException;
import com.wolfhouse.wolfhouseblog.common.utils.imageutil.ImgValidator;
import com.wolfhouse.wolfhouseblog.common.utils.oss.BucketClient;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.common.utils.result.HttpCode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.NotAnyBlankVerifyNode;
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
import com.wolfhouse.wolfhouseblog.redis.UserRedisService;
import com.wolfhouse.wolfhouseblog.service.OssUploadLogService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.SubscribeTableDef.SUBSCRIBE;
import static com.wolfhouse.wolfhouseblog.pojo.domain.table.UserTableDef.USER;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServicesImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final SubscribeMapper subscribeMapper;
    private final ServiceAuthMediator mediator;
    private final UserAuthService authService;
    private final JwtUtil jwtUtil;
    private final BucketClient avatarOssClient;
    private final ImgValidator avatarValidator;
    private final OssUploadLogService ossLogService;
    private final UserRedisService redisService;

    @Resource(name = "defaultObjectMapper")
    private ObjectMapper defaultMapper;


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
                .email(dto.getEmail()
                          .toLowerCase())
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
    @SuppressWarnings("unchecked")
    public UserVo updateAuthedUser(UserUpdateDto dto) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        // 验证 DTO
        VerifyTool.of(
                      UserVerifyNode.id(mediator)
                                    .target(login),
                      UserVerifyNode.BIRTH.target(dto.getBirth())
                                          .allowNull(true),
                      UserVerifyNode.email(this)
                                    .target(dto.getEmail())
                                    .allowNull(false))
                  .doVerify();

        // 已有用户和更新用户数据
        Map<String, Object> userMap = defaultMapper.convertValue(getById(login), Map.class);
        Map<String, Object> updateMap = defaultMapper.convertValue(dto, Map.class);

        // 获取头像
        String avatar = dto.getAvatar() == null ? null : redisService.getUserAvatar(login);
        if (avatar == null) {
            // 不覆盖更新头像
            updateMap.remove(USER.AVATAR.getName());
        }
        // 合并数据
        userMap.putAll(updateMap);

        User user = defaultMapper.convertValue(userMap, User.class);

        if (mapper.update(user, false) != 1) {
            throw new ServiceException(UserConstant.USER_UPDATE_FAILED);
        }
        return BeanUtil.copyProperties(getUserVoById(user.getId()), UserVo.class);
    }

    @Override
    public String generateAccount(String username, Integer codeLen) {
        if (username == null) {
            throw new ServiceException(ServiceExceptionConstant.SERVER_ERROR);
        }
        int countCode = new Random().nextInt((int) Math.pow(10, codeLen - 1), (int) Math.pow(10, codeLen));
        String account = username.toLowerCase() + UserConstant.ACCOUNT_SEPARATOR;
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
    public List<UserVo> getUsers(Set<Long> ids) throws Exception {
        // 需要登录, ids 不得为空
        VerifyTool.ofLoginExist(mediator,
                                new NotAnyBlankVerifyNode(ids)
                                    .exception(new ServiceException(ServiceExceptionConstant.ARG_FORMAT_ERROR)))
                  .doVerify();
        List<User> users = mapper.selectListByQuery(
            QueryWrapper.create()
                        .where(USER.ID.in(ids)));
        return BeanUtil.copyList(users, UserVo.class);
    }

    @Override
    public Boolean hasAccountOrEmail(String s) {
        var sLowerCase = s.toLowerCase();
        long count = mapper.selectCountByQuery(
            new QueryWrapper().eq(User::getAccount, sLowerCase)
                              .or((Consumer<QueryWrapper>) w -> w.eq(User::getEmail, sLowerCase)));
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

    @Override
    public UserBriefVo getUserBriefById(Long authorId) {
        return mapper.selectOneByQueryAs(
            QueryWrapper.create()
                        .select(UserBriefVo.COLUMNS)
                        .where(USER.ID.eq(authorId)), UserBriefVo.class);
    }

    /**
     * 生成唯一的文件名，格式为 userId_随机字符串.格式。若生成的文件名已存在，
     * 则重新生成，直到文件名唯一或达到最大重试次数。
     *
     * @param userId 用户ID，用于文件名前缀。
     * @param format 文件格式，例如 "jpg"、"png"。
     * @return 生成的唯一文件名。
     * @throws ServiceException 如果在达到最大重试次数后仍无法生成唯一文件名，抛出此异常。
     */
    private String genFilename(Long userId, String format) {
        String filename = String.format("%s_%s.%s", userId, RandomUtil.randomString(16), format);
        int maxReties = 10;
        // 文件是否已存在，若已存在则重新生成名称
        while (avatarOssClient.doesObjectExist(filename) && maxReties-- > 0) {
            filename = String.format("%s_%s.%s", userId, RandomUtil.randomString(16), format);
        }
        if (maxReties <= 0) {
            // 超出最大重试次数
            throw new ServiceException(HttpCode.OSS_UPLOAD_FAILED.message);
        }
        return filename;
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws ImgValidException {
        // 0. 校验登录信息
        Long userId = mediator.loginUserOrE();
        // 1. 校验文件
        ImgValidator.Result fileValid = avatarValidator.validate(file);
        if (!fileValid.valid()) {
            // 解析图片失败
            log.error("{}: {}", UserConstant.AVATAR_VALID_FAILED, fileValid.message());
            throw new ServiceException(UserConstant.AVATAR_VALID_FAILED);
        }

        // 2. 生成文件名称
        // 通过登录用户 ID + 随机字符串生成
        String filename = genFilename(userId, UserConstant.AVATAR_FORMAT);
        // 文件存储路径
        String filepath = avatarOssClient.getFileUploadPath(filename);
        // 文件字节输入流
        ByteArrayInputStream imgIns;
        // 3. 上传文件
        try (var ins = file.getInputStream()) {
            // TODO Redis 获取已上传图片路径，判断是否有已经上传但没有使用的头像指纹，有则删除

            // 3.1 压缩图片大小
            // 构建压缩器
            ImgCompressor compressor = ImgCompressor.of(ins);
            // 判断是否需要压缩质量
            if (fileValid.size() > UserConstant.AVATAR_COMPRESS_SIZE) {
                compressor.quality(UserConstant.AVATAR_COMPRESS_QUALITY);
            }
            // 压缩宽高
            compressor.scale(UserConstant.AVATAR_MAX_WIDTH, UserConstant.AVATAR_MAX_HEIGHT);
            // 转换格式
            compressor.format(UserConstant.AVATAR_FORMAT);
            // 执行压缩，获取字节并转为字节流
            // TODO 不同图片大小使用不同压缩比例
            imgIns = new ByteArrayInputStream(compressor.doCompress()
                                                        .toByteArray());

        } catch (IOException e) {
            log.error("{}: {}", ServiceExceptionConstant.EXEC_FAILED, e.getMessage(), e);
            throw new ServiceException(HttpCode.IO_ERROR.message);
        }
        // 3.2 上传文件
        // 上传文件，可公开读
        avatarOssClient.putStream(filename, imgIns, false, CannedAccessControlList.PublicRead);
        // 文件不存在
        if (!avatarOssClient.doesObjectExist(filename)) {
            throw new ServiceException(HttpCode.OSS_UPLOAD_FAILED.message);
        }
        // 记录上传日志
        if (!ossLogService.log(filename, fileValid.size(), filepath)) {
            log.error("{}: \nname: {}\npath: {}\nsize: {}\nuserId: {}",
                      OssUploadLogConstant.FAIL_TO_LOG,
                      filename,
                      filepath,
                      fileValid.size(),
                      userId);
        }


        // 4. 返回地址
        return filepath;
    }
}

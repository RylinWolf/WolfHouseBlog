package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.BlogPermissionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JsonNullableUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.admin.AdminVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.AdminMapper;
import com.wolfhouse.wolfhouseblog.mq.service.MqUserService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUserControlDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AuthorityByIdDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqUserAuthDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.AdminVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.AuthorityVo;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.AuthorityService;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.AdminTableDef.ADMIN;

/**
 * @author linexsong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    private final AuthorityService authService;
    private final MqUserService mqUserService;
    private final ServiceAuthMediator mediator;

    @PostConstruct
    private void init() {
        this.mediator.registerAdmin(this);
    }


    @Override
    public Optional<Admin> getAdminById(Long id) {
        return Optional.ofNullable(mapper.selectOneById(id));
    }

    @Override
    public Optional<Admin> getAdminByUserId(Long userId) {
        return Optional.ofNullable(mapper.selectOneByQuery(new QueryWrapper().eq(Admin::getUserId, userId)));
    }

    @Override
    public AdminVo getAdminVoById(Long id) throws Exception {
        AdminVo vo = BeanUtil.copyProperties(mapper.selectOneById(id), AdminVo.class);
        vo.setAuthorities(getAuthoritiesByAdminId(id));
        return vo;
    }

    @Override
    public Boolean isUserAdmin(Long userId) {
        return mapper.selectCountByQuery(new QueryWrapper().eq(Admin::getUserId, userId)) > 0;
    }

    @Override
    public List<Long> getAuthoritiesIds(Long userId) throws Exception {
        // 获取管理员
        Optional<Admin> admin = getAdminByUserId(userId);
        if (admin.isEmpty()) {
            return Collections.emptyList();
        }

        // 通过管理员 ID 获取权限
        List<Long> authIds = getAuthoritiesIdsByAdmin(admin.get()
                                                           .getId());

        if (authIds.isEmpty()) {
            return Collections.emptyList();
        }

        return authIds;
    }

    @Override
    public List<Authority> getAuthorities(Long userId) throws Exception {
        var authIds = getAuthoritiesIds(userId);
        if (authIds.isEmpty()) {
            return Collections.emptyList();
        }
        var res = authService.getMapper()
                             .selectListByIds(authIds);
        res.add(Authority.builder()
                         .permissionCode(BlogPermissionConstant.ROLE_ADMIN)
                         .permissionName(BlogPermissionConstant.ADMIN_NAME)
                         .build());
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminVo createAdmin(AdminPostDto dto) throws Exception {
        // 登录用户应为管理员
        Long login = ServiceUtil.loginUserOrE();
        if (!isUserAdmin(login)) {
            throw ServiceException.notAllowed();
        }

        // 验证创建管理员的用户 ID 是否存在，是否已经是管理员
        VerifyTool.ofLoginExist(
                      mediator,
                      UserVerifyNode.id(mediator)
                                    .target(dto.getUserId()),
                      AdminVerifyNode.createId(mediator)
                                     .target(dto.getUserId()))
                  .doVerify();

        Admin admin = BeanUtil.copyProperties(dto, Admin.class);
        int insert = mapper.insert(admin);
        return insert != 1 ? null : getAdminVoById(admin.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminVo updateAdmin(AdminUpdateDto dto) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        Long adminId = dto.getId();

        String name = JsonNullableUtil.getObjOrNull(dto.getName());

        // 权限
        JsonNullable<List<Long>> authoritiesNullable = dto.getAuthorities();
        List<Long> authorityList = JsonNullableUtil.getObjOrNull(authoritiesNullable);
        Long[] authorities = authorityList == null ? null : authorityList.toArray(new Long[0]);

        // 获取当前登录用户 验证权限
        VerifyTool.ofLoginExist(
                      mediator,
                      UserVerifyNode.id(mediator)
                                    .target(login),
                      // 登陆用户是否为管理员
                      AdminVerifyNode.userId(mediator)
                                     .target(login)
                                     .exception(AuthExceptionConstant.ACCESS_DENIED),
                      // 至少有一个数据更新
                      new NotAllBlankVerifyNode(name, authorities)
                          .exception(new ServiceException(VerifyConstant.NOT_ALL_BLANK)),
                      // 更新目标需要是有效的管理员
                      AdminVerifyNode.id(mediator)
                                     .target(adminId),
                      // 管理员名称验证
                      AdminVerifyNode.NAME.target(name)
                                          .allowNull(true),
                      // 权限验证
                      AdminVerifyNode.authorityId(mediator)
                                     .target(authorities)
                                     .allowNull(true))
                  .doVerify();

        // 修改权限
        authoritiesNullable.ifPresent(a -> {
            try {
                Integer i = authService.changeAuthorities(new AuthorityByIdDto(adminId, new HashSet<>(a)));
                if (i == 0) {
                    log.warn("管理员[{}]没有任何权限修改", adminId);
                }
            } catch (Exception e) {
                throw new ServiceException(e.getMessage(), e);
            }
        });

        // 修改其他信息
        UpdateChain<Admin> chain = UpdateChain.of(Admin.class)
                                              .where(ADMIN.ID.eq(adminId));
        dto.getName()
           .ifPresent(n -> chain.set(ADMIN.NAME, n, n != null));
        boolean update = chain.update();

        // 目前管理员只有名称可以被修改，因此如果没有修改则没有更新项
        if (!authoritiesNullable.isPresent() && !update) {
            return null;
        }

        return getAdminVoById(adminId);
    }

    @Override
    public Boolean isAuthoritiesExist(Long... authorityIds) {
        long count = authService.getMapper()
                                .selectCountByQuery(new QueryWrapper().in(Authority::getId, List.of(authorityIds)));

        return count == authorityIds.length;
    }

    @Override
    public List<Long> getAuthoritiesIdsByAdmin(Long adminId) throws Exception {
        VerifyTool.of(AdminVerifyNode.id(mediator)
                                     .target(adminId))
                  .doVerify();

        return authService.getAuthoritiesIds(adminId)
                          .stream()
                          .toList();
    }

    @Override
    public List<AuthorityVo> getAuthoritiesByAdminId(Long adminId) throws Exception {
        List<Long> ids = getAuthoritiesIdsByAdmin(adminId);
        return ids.isEmpty() ? List.of() : BeanUtil.copyList(
            authService.getMapper()
                       .selectListByIds(ids), AuthorityVo.class);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long adminId) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        if (!isUserAdmin(login)) {
            throw ServiceException.notAllowed();
        }

        VerifyTool.of(
                      UserVerifyNode.id(mediator)
                                    .target(login),
                      AdminVerifyNode.id(mediator)
                                     .target(adminId))
                  .doVerify();

        if (mapper.deleteById(adminId) != 1) {
            throw new ServiceException(AdminConstant.DELETE_FAILED);
        }

        Integer i = authService.deleteAllAuthorities(adminId);
        if (i == 0) {
            log.warn("没有删除管理[{}]的任何权限", adminId);
        }
        return true;
    }

    @Override
    public Boolean deleteUser(AdminUserControlDto dto) throws Exception {
        Long login = mediator.loginUserOrE();
        VerifyTool.of(
                      AdminVerifyNode.userId(mediator)
                                     .target(login),
                      UserVerifyNode.id(mediator)
                                    .target(dto.getUserId())
                                    .exception(UserConstant.USER_NOT_EXIST),
                      UserVerifyNode.pwd(mediator)
                                    .userId(login)
                                    .target(dto.getPassword()))
                  .doVerify();

        MqUserAuthDto authDto = new MqUserAuthDto(dto.getUserId());
        authDto.setLoginId(login);

        mqUserService.deleteUser(authDto);
        return true;
    }

    @Override
    public Boolean disableUser(AdminUserControlDto dto) throws Exception {
        Long login = mediator.loginUserOrE();
        VerifyTool.of(
                      // 验证管理员是否存在
                      AdminVerifyNode.userId(mediator)
                                     .target(login),
                      // 验证用户是否存在
                      UserVerifyNode.id(mediator)
                                    .target(dto.getUserId()),
                      // 验证密码是否正确
                      UserVerifyNode.pwd(mediator)
                                    .userId(login)
                                    .target(dto.getPassword()))
                  .doVerify();

        MqUserAuthDto authDto = new MqUserAuthDto(dto.getUserId());
        authDto.setLoginId(login);
        mqUserService.disableUser(authDto);
        return true;
    }

    @Override
    public Boolean enableUser(AdminUserControlDto dto) throws Exception {
        Long login = mediator.loginUserOrE();
        Long userId = dto.getUserId();

        VerifyTool.of(
                      UserVerifyNode.pwd(mediator)
                                    .userId(login)
                                    .target(dto.getPassword()),
                      AdminVerifyNode.userId(mediator)
                                     .target(login),
                      // 用户不存在
                      new BaseVerifyNode<Long>() {}
                          .predicate(mediator::isAuthExist)
                          .target(userId)
                          .exception(new ServiceException(UserConstant.USER_NOT_EXIST)),
                      // 账号已启用
                      new BaseVerifyNode<Long>() {}
                          .predicate(u -> !mediator.isUserEnabled(u))
                          .target(userId)
                          .exception(new ServiceException(UserConstant.USER_HAS_ENABLED)))
                  .doVerify();
        return mqUserService.enableUser(userId);
    }
}

package com.wolfhouse.wolfhouseblog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin.AdminVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JsonNullableUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.AdminMapper;
import com.wolfhouse.wolfhouseblog.mapper.AuthorityMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.AdminVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.AuthorityVo;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author linexsong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    private final AdminMapper mapper;
    private final AuthorityMapper authorityMapper;
    private final UserAuthService authService;
    @Resource(name = "jsonNullableObjectMapper")
    private ObjectMapper objectMapper;


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
        return authIds.isEmpty() ? List.of() : authorityMapper.selectListByIds(authIds);
    }

    @Override
    public AdminVo createAdmin(AdminPostDto dto) throws Exception {
        // 登录用户应为管理员
        Long login = ServiceUtil.loginUserOrE();
        if (!isUserAdmin(login)) {
            throw ServiceException.notAllowed();
        }

        // 验证创建管理员的用户 ID 是否存在，是否已经是管理员
        VerifyTool.ofLoginExist(
                       authService,
                       UserVerifyNode.id(authService)
                                     .target(dto.getUserId()),
                       AdminVerifyNode.createId(this, authService)
                                      .target(dto.getUserId()))
                  .doVerify();

        Admin admin = BeanUtil.copyProperties(dto, Admin.class);
        return mapper.insert(admin) != 1 ? null : getAdminVoById(admin.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminVo updateAdmin(AdminUpdateDto dto) throws Exception {
        String name = JsonNullableUtil.getObjOrNull(dto.getName());
        Long adminId = dto.getId();

        // 权限
        List<Long> authorityList = JsonNullableUtil.getObjOrNull(dto.getAuthorities());
        Long[] authorities = authorityList == null ? null : authorityList.toArray(new Long[0]);

        // 获取当前登录用户 验证权限
        VerifyTool.ofLoginExist(
                       authService,
                       // 登陆用户是否为管理员
                       AdminVerifyNode.userId(this)
                                      .target(ServiceUtil.loginUser()),
                       // 至少有一个数据更新
                       new NotAllBlankVerifyNode(name, authorities)
                            .exception(new ServiceException(VerifyConstant.NOT_ALL_BLANK)),
                       // 更新目标需要是有效的管理员
                       AdminVerifyNode.id(this)
                                      .target(adminId),
                       // 管理员名称验证
                       AdminVerifyNode.NAME.target(JsonNullableUtil.getObjOrNull(dto.getName())),
                       // 权限验证
                       AdminVerifyNode.authorityId(this)
                                      .target(authorities))
                  .doVerify();

        // 修改权限
        Integer i = changeAuthorities(adminId, authorities);

        // 修改其他信息
        Admin admin = objectMapper.convertValue(dto, Admin.class);

        // 目前管理员只有名称可以被修改，因此如果没有修改则没有更新项
        if (name == null) {
            return null;
        }

        if (mapper.update(admin, true) != 1 && i == 0) {
            return null;
        }

        return getAdminVoById(admin.getId());
    }

    @Override
    public Boolean isAuthoritiesExist(Long... authorityIds) {
        long count = authorityMapper.selectCountByQuery(new QueryWrapper().in(Authority::getId, List.of(authorityIds)));

        return count == authorityIds.length;
    }

    @Override
    public List<Long> getAuthoritiesIdsByAdmin(Long adminId) throws Exception {
        VerifyTool.of(AdminVerifyNode.id(this)
                                     .target(adminId))
                  .doVerify();

        return List.of(authorityMapper.getIdsByAdminId(adminId));
    }

    @Override
    public List<AuthorityVo> getAuthoritiesByAdminId(Long adminId) throws Exception {
        List<Long> ids = getAuthoritiesIdsByAdmin(adminId);
        return ids.isEmpty() ? List.of() : BeanUtil.copyList(authorityMapper.selectListByIds(ids), AuthorityVo.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer changeAuthorities(Long adminId, Long[] newAuthIds) throws Exception {
        // 0. 验证
        Long login = ServiceUtil.loginUserOrE();
        VerifyTool.of(
                       // 0.1 登录用户是否存在
                       UserVerifyNode.id(authService)
                                     .target(login),
                       // 0.2 登陆用户是否为管理员
                       AdminVerifyNode.id(this)
                                      .target(login),
                       // 0.3 修改的管理员是否存在
                       AdminVerifyNode.id(this)
                                      .target(adminId),
                       // 0.4 权限列表是否存在
                       AdminVerifyNode.authorityId(this)
                                      .target(newAuthIds))
                  .doVerify();

        // 1.得到新增和重复权限
        List<Long> authIds = new ArrayList<>(getAuthoritiesIdsByAdmin(adminId));
        // 1.0 原权限列表为空
        if (authIds.isEmpty()) {
            if (newAuthIds.length == 0) {
                return 0;
            }
            return authorityMapper.addAuthorities(adminId, newAuthIds);
        }
        // 1.1 对比列表
        Set<Long> newAuthIdsSet = new HashSet<>(List.of(newAuthIds));
        List<Long> repeatIds = new ArrayList<>();

        newAuthIdsSet.forEach(a -> {
            if (authIds.contains(a)) {
                repeatIds.add(a);
            }
        });

        // 1.2 得到重复项，从列表中去除
        repeatIds.forEach(a -> {
            authIds.remove(a);
            newAuthIdsSet.remove(a);
        });

        // 2. 删除权限
        Integer removeCount = 0;
        // 去除列表即为去重后的原权限列表
        if (!authIds.isEmpty()) {
            removeCount = authorityMapper.removeAuthByAdmin(adminId, authIds);
        }

        // 3. 新增权限
        // 新增列表即位去重后的新权限列表
        Integer addCount = 0;
        if (!newAuthIdsSet.isEmpty()) {
            addCount = authorityMapper.addAuthorities(adminId, newAuthIdsSet.toArray(new Long[0]));
        }

        if (!(removeCount.equals(authIds.size()) && addCount.equals(newAuthIdsSet.size()))) {
            log.error(
                 "removeCount: {}, addCount: {}" +
                 "repeatedIds: {}, newAuthIdsSet: {} ",
                 removeCount, addCount, repeatIds, newAuthIdsSet);
            throw new ServiceException(AdminConstant.AUTHORITIES_CHANGE_FAILED);
        }
        return removeCount + addCount;
    }
}

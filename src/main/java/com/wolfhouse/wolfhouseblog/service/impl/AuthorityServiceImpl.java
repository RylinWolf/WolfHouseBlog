package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.AuthorityConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.admin.AdminVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.AdminAuthMapper;
import com.wolfhouse.wolfhouseblog.mapper.AuthorityMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.domain.table.AuthorityTableDef;
import com.wolfhouse.wolfhouseblog.pojo.dto.AuthorityByIdDto;
import com.wolfhouse.wolfhouseblog.service.AuthorityService;
import com.wolfhouse.wolfhouseblog.service.mediator.ServiceAuthMediator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.AdminAuthTableDef.ADMIN_AUTH;

/**
 * @author linexsong
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements AuthorityService {
    private final ServiceAuthMediator mediator;
    private final AdminAuthMapper adminAuthMapper;

    @Override
    public Boolean addAuthorityByIds(AuthorityByIdDto dto) {
        Long adminId = dto.getAdminId();
        adminId = adminId == null ? ServiceUtil.loginUser() : adminId;

        // 验证管理员是否存在
        if (!mediator.isAdminExist(adminId)) {
            throw new ServiceException(AdminConstant.ADMIN_NOT_EXIST);
        }

        Set<Long> ids = dto.getIds();
        // 权限 ids 为空
        if (BeanUtil.isBlank(ids)) {
            throw new ServiceException(AuthExceptionConstant.BAD_REQUEST);
        }

        // 权限不存在
        if (mapper.selectCountByQuery(QueryWrapper.create()
                                                  .where(AuthorityTableDef.AUTHORITY.ID.in(ids))) != ids.size()) {
            throw new ServiceException(AuthorityConstant.NOT_EXIST);
        }

        // 权限已拥有
        if (CollectionUtils.containsAny(List.of(mapper.getIdsByAdminId(adminId)), ids)) {
            throw new ServiceException(AuthorityConstant.HAS_EXIST);
        }

        if (adminAuthMapper.addByIds(adminId, ids) != ids.size()) {
            throw new ServiceException(AuthorityConstant.ADD_FAILED);
        }
        return true;
    }

    @Override
    public Boolean deleteAuthorityByIds(AuthorityByIdDto dto) {
        Set<Long> ids = dto.getIds();
        Long admin = dto.getAdminId();
        if (BeanUtil.isAnyBlank(ids, admin)) {
            throw new ServiceException(AuthExceptionConstant.BAD_REQUEST);
        }
        if (adminAuthMapper.deleteByQuery(
            QueryWrapper.create()
                        .where(ADMIN_AUTH.AUTH_ID.in(ids))
                        .and(ADMIN_AUTH.ADMIN_ID.eq(admin))) != ids.size()) {
            throw new ServiceException(AuthorityConstant.NOT_EXIST);
        }
        return true;
    }

    @Override
    public List<Authority> updateAuthority(AuthorityByIdDto dto) throws Exception {
        Long adminId = dto.getAdminId();
        Set<Long> ids = dto.getIds();
        ids = ids == null ? new HashSet<>() : ids;
        VerifyTool.of(
                      // 管理员是否存在
                      AdminVerifyNode.id(mediator)
                                     .target(adminId),
                      // 权限是否存在
                      AdminVerifyNode.authorityId(mediator)
                                     .target(ids.toArray(Long[]::new))
                                     .allowNull(true))
                  .doVerify();
        // 移除所有权限
        if (BeanUtil.isBlank(ids)) {
            if (adminAuthMapper.deleteAllByAdmin(adminId) == 0) {
                // 无权限
                return List.of();
            }
            List<Long> hasIds = adminAuthMapper.getIdsByAdminId(adminId);
            return mapper.selectListByIds(hasIds);
        }
        if (!mediator.isAuthoritiesExist(ids.toArray(Long[]::new))) {
            throw new ServiceException(AuthorityConstant.NOT_EXIST);
        }

        // 设置权限
        return List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer changeAuthorities(AuthorityByIdDto dto) throws Exception {
        Long adminId = dto.getAdminId();
        Set<Long> newAuthIds = dto.getIds();

        if (!BeanUtil.isAnyNotBlank(adminId, newAuthIds)) {
            // 全为空
            throw new ServiceException(AuthExceptionConstant.BAD_REQUEST);
        }

        adminId = adminId == null ? ServiceUtil.loginUserOrE() : adminId;
        newAuthIds = newAuthIds == null ? new HashSet<>() : newAuthIds;

        // 0. 验证
        VerifyTool.of(
                      // 0.1 登录用户是否存在
                      UserVerifyNode.id(mediator)
                                    .target(adminId),
                      // 0.2 登陆用户是否为管理员
                      AdminVerifyNode.id(mediator)
                                     .target(adminId),
                      // 0.3 修改的管理员是否存在
                      AdminVerifyNode.id(mediator)
                                     .target(adminId),
                      // 0.4 权限列表是否存在
                      AdminVerifyNode.authorityId(mediator)
                                     .target(newAuthIds
                                                 .toArray(Long[]::new))
                                     .allowNull(true))
                  .doVerify();

        // 0.1 修改权限列表为空
        if (BeanUtil.isBlank(newAuthIds)) {
            Integer removed = mapper.removeAllByAdmin(adminId);
            // 移除数量，应当为 removed
            return removed - adminAuthMapper.getIdsByAdminId(adminId)
                                            .size();
        }

        // 1.得到新增和重复权限
        List<Long> authIds = adminAuthMapper.getIdsByAdminId(adminId);
        // 1.0 原权限列表为空
        if (authIds.isEmpty()) {
            if (newAuthIds.isEmpty()) {
                return 0;
            }
            return mapper.addAuthorities(adminId, newAuthIds.toArray(new Long[0]));
        }
        // 1.1 对比列表
        Set<Long> newAuthIdsSet = new HashSet<>(newAuthIds);
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
            removeCount = mapper.removeAuthByAdmin(adminId, authIds);
        }

        // 3. 新增权限
        // 新增列表即位去重后的新权限列表
        Integer addCount = 0;
        if (!newAuthIdsSet.isEmpty()) {
            addCount = mapper.addAuthorities(adminId, newAuthIdsSet.toArray(new Long[0]));
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

    @Override
    public Set<Long> getAuthoritiesIds(Long adminId) {
        return Set.of(mapper.getIdsByAdminId(adminId));
    }

    @Override
    public Integer deleteAllAuthorities(Long adminId) {
        return mapper.removeAllByAdmin(adminId);
    }
}

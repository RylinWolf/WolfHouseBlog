package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.AuthorityConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.AdminAuthMapper;
import com.wolfhouse.wolfhouseblog.mapper.AuthorityMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.domain.table.AuthorityTableDef;
import com.wolfhouse.wolfhouseblog.pojo.dto.AuthorityByIdDto;
import com.wolfhouse.wolfhouseblog.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.AdminAuthTableDef.ADMIN_AUTH;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements AuthorityService {
    private final AdminAuthMapper adminAuthMapper;

    @Override
    public Boolean addAuthorityByIds(AuthorityByIdDto dto) {
        Long adminId = dto.getAdminId();
        adminId = adminId == null ? ServiceUtil.loginUser() : adminId;

        // TODO 管理员验证
        // TODO 通过中介者模式，提取出验证类的相关方法，统一放在一个中介类中 mediator
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
}

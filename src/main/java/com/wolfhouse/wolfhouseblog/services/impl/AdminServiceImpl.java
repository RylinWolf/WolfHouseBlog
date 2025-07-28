package com.wolfhouse.wolfhouseblog.services.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.mapper.AdminMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper mapper;

    @Override
    public Optional<Admin> getAdminById(Long id) {
        return Optional.ofNullable(mapper.selectOneById(id));
    }

    @Override
    public Optional<Admin> getAdminByUserId(Long userId) {
        return Optional.ofNullable(mapper.selectOneByQuery(new QueryWrapper().eq(Admin::getUserId, userId)));
    }

    @Override
    public Boolean isUserAdmin(Long userId) {
        return mapper.selectCountByQuery(new QueryWrapper().eq(Admin::getUserId, userId)) > 0;
    }

    @Override
    public List<Authority> getAuthorities(Long userId) {
        return Optional.ofNullable(mapper.selectOneByQuery(new QueryWrapper().eq(Admin::getUserId, userId)))
                       .orElse(new Admin())
                       .getAuthorities();
    }
}

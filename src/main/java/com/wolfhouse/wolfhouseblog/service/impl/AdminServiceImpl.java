package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin.AdminVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.AdminMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Admin;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper mapper;
    private final UserAuthService authService;

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
        return isUserAdmin(userId) ? Optional.ofNullable(mapper.selectOneByQuery(new QueryWrapper().eq(
                                                     Admin::getUserId,
                                                     userId)))
                                             .orElse(new Admin())
                                             .getAuthorities() : Collections.emptyList();
    }

    @Override
    public Boolean createAdmin(AdminPostDto dto) throws Exception {
        // 登录用户应为管理员
        Long login = ServiceUtil.loginUserOrE();
        if (!isUserAdmin(login)) {
            throw ServiceException.notAllowed();
        }
        VerifyTool.ofLoginExist(
                          authService,
                          UserVerifyNode.id(authService)
                                        .target(dto.getUserId()),
                          AdminVerifyNode.id(this, authService)
                                         .target(dto.getUserId()))
                  .doVerify();

        Admin admin = BeanUtil.copyProperties(dto, Admin.class);
        return mapper.insert(admin) == 1;
    }
}

package com.wolfhouse.wolfhouseblog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.admin.AdminVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user.UserVerifyNode;
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
import com.wolfhouse.wolfhouseblog.service.AdminService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import jakarta.annotation.Resource;
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
    public AdminVo getAdminVoById(Long id) {
        return BeanUtil.copyProperties(mapper.selectOneById(id), AdminVo.class);
    }

    @Override
    public Boolean isUserAdmin(Long userId) {
        return mapper.selectCountByQuery(new QueryWrapper().eq(Admin::getUserId, userId)) > 0;
    }

    @Override
    public List<Authority> getAuthorities(Long userId) {
        List<Long> authIds = isUserAdmin(userId) ? Optional.ofNullable(mapper.selectOneByQuery(new QueryWrapper().eq(
                                                                   Admin::getUserId,
                                                                   userId)))
                                                           .orElse(new Admin())
                                                           .getAuthorities() : Collections.emptyList();
        if (authIds.isEmpty()) {
            return Collections.emptyList();
        }

        return authorityMapper.selectListByIds(authIds);
    }

    @Override
    public AdminVo createAdmin(AdminPostDto dto) throws Exception {
        // 登录用户应为管理员
        Long login = ServiceUtil.loginUserOrE();
        if (!isUserAdmin(login)) {
            throw ServiceException.notAllowed();
        }
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
    public AdminVo updateAdmin(AdminUpdateDto dto) throws Exception {
        // 获取当前登录用户 验证权限
        VerifyTool.ofLoginExist(
                          authService,
                          // 登陆用户是否为管理员
                          AdminVerifyNode.userId(this)
                                         .target(ServiceUtil.loginUser()),
                          // 至少有一个数据更新
                          new NotAllBlankVerifyNode(
                                  JsonNullableUtil.getObjOrNull(dto.getName()),
                                  JsonNullableUtil.getObjOrNull(dto.getAuthorities())),
                          // 更新目标需要是有效的管理员
                          AdminVerifyNode.id(this)
                                         .target(dto.getId()),
                          // 管理员名称验证
                          AdminVerifyNode.NAME.target(JsonNullableUtil.getObjOrNull(dto.getName())))
                  .doVerify();

        // 根据 Id 修改
        Admin admin = objectMapper.convertValue(dto, Admin.class);

        return mapper.update(admin, true) != 1 ? null : getAdminVoById(admin.getId());
    }
}

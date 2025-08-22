package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.AuthorityConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.BlogPermissionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.AuthorityByIdDto;
import com.wolfhouse.wolfhouseblog.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 权限相关接口
 * 给管理员分配不同权限
 *
 * @author linexsong
 */
@RestController
@RequestMapping("/api/a/auth")
@RequiredArgsConstructor
@PreAuthorize("@ss.hasRole('" + BlogPermissionConstant.SUPER_ADMIN + "')")
public class AuthorityController {
    private final AuthorityService service;

    @PostMapping
    @PreAuthorize("@ss.hasAnyPerm('admin:authority:create', 'admin:authority:update')")
    public HttpResult<?> addAuthority(@RequestBody AuthorityByIdDto dto) {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             AuthorityConstant.ADD_FAILED,
             service.addAuthorityByIds(dto));
    }

    @DeleteMapping
    @PreAuthorize("@ss.hasPerm('" + BlogPermissionConstant.AUTHORITY_DELETE + "')")
    public HttpResult<?> deleteAuthority(@RequestBody AuthorityByIdDto dto) {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             AuthorityConstant.DELETE_FAILED,
             service.deleteAuthorityByIds(dto));
    }

    // TODO 根据 ID 删除

    // TODO 根据名称删除


}

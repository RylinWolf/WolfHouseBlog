package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUserDeleteDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.AdminVo;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/api/a")
@RequiredArgsConstructor
@Tag(name = "管理员接口")
public class AdminController {
    private final AdminService service;

    @GetMapping("/au")
    @Operation(summary = "获取权限列表")
    public HttpResult<List<Long>> getAuthorities() throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        if (!service.isUserAdmin(login)) {
            return HttpResult.failed(
                 HttpCodeConstant.ACCESS_DENIED,
                 AuthExceptionConstant.ACCESS_DENIED);
        }
        return HttpResult.success(service.getAuthoritiesIds(login));
    }

    @PostMapping
    @Operation(summary = "添加管理员")
    public HttpResult<AdminVo> postAdmin(@RequestBody AdminPostDto dto) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             AdminConstant.CREATE_FAILED,
             service.createAdmin(dto));
    }

    @PutMapping
    @Operation(summary = "更新管理员")
    public HttpResult<AdminVo> updateAdmin(@RequestBody AdminUpdateDto dto) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.UPDATE_FAILED,
             AdminConstant.UPDATE_FAILED,
             service.updateAdmin(dto));
    }

    @DeleteMapping(value = "/{adminId}")
    @Operation(summary = "删除管理员")
    public HttpResult<?> deleteAdmin(@PathVariable Long adminId) throws Exception {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             AdminConstant.DELETE_FAILED,
             service.delete(adminId));
    }

    @DeleteMapping("/user")
    @Operation(summary = "删除用户")
    public HttpResult<?> deleteUser(@RequestBody AdminUserDeleteDto dto) throws Exception {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             UserConstant.DELETE_FAILED,
             service.deleteUser(dto));
    }

    @DeleteMapping("/user/disable")
    @Operation(summary = "禁用用户")
    public HttpResult<?> disableUser(@RequestBody AdminUserDeleteDto dto) throws Exception {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             UserConstant.DISABLE_FAILED,
             service.disableUser(dto));
    }
}

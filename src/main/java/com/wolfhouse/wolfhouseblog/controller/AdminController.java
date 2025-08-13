package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpMediaTypeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUserControlDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.AdminVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.AuthorityVo;
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
    public HttpResult<List<AuthorityVo>> getAuthorities() throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        if (!service.isUserAdmin(login)) {
            return HttpResult.failed(
                 HttpCodeConstant.ACCESS_DENIED,
                 AuthExceptionConstant.ACCESS_DENIED);
        }
        return HttpResult.success(BeanUtil.copyList(service.getAuthorities(login), AuthorityVo.class));
    }

    @PostMapping
    @Operation(summary = "添加管理员")
    public HttpResult<AdminVo> postAdmin(@RequestBody AdminPostDto dto) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             AdminConstant.CREATE_FAILED,
             service.createAdmin(dto));
    }

    @PatchMapping(consumes = {HttpMediaTypeConstant.APPLICATION_JSON_NULLABLE_VALUE})
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
    public HttpResult<?> deleteUser(@RequestBody AdminUserControlDto dto) throws Exception {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             UserConstant.DELETE_FAILED,
             service.deleteUser(dto));
    }

    @DeleteMapping("/user/disable")
    @Operation(summary = "禁用用户")
    public HttpResult<?> disableUser(@RequestBody AdminUserControlDto dto) throws Exception {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             UserConstant.DISABLE_FAILED,
             service.disableUser(dto));
    }
}

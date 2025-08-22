package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserLoginDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserSubDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserLoginVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserRegisterVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationManager authManager;
    private final UserAuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "登陆")
    @PostMapping("/login")
    public ResponseEntity<HttpResult<UserLoginVo>> login(@RequestBody UserLoginDto dto) {
        try {
            Authentication auth = authManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                 dto.getAccount(),
                 dto.getPassword()));
            log.info("用户[{}]登陆", auth.getPrincipal());
            return HttpResult.ok(UserLoginVo.token(jwtUtil.getToken(auth)), null);

        } catch (AuthenticationException e) {
            // 验证失败
            return HttpResult.failed(
                 HttpStatus.UNAUTHORIZED.value(),
                 HttpCodeConstant.AUTH_FAILED,
                 AuthExceptionConstant.AUTHENTIC_FAILED,
                 null);
        }
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<HttpResult<UserRegisterVo>> register(@RequestBody @Valid UserRegisterDto dto)
         throws Exception {
        log.info("用户注册: {}", dto);
        // 检查用户是否存在
        if (userService.hasAccountOrEmail(dto.getEmail())) {
            return HttpResult.failed(
                 HttpStatus.CONFLICT.value(),
                 HttpCodeConstant.USER_ALREADY_EXIST,
                 UserConstant.USER_ALREADY_EXIST,
                 null);
        }
        // 设置用户 ID
        dto.setUserId(authService.createAuth(dto.getPassword())
                                 .getUserId());

        // 注册用户
        return HttpResult.failedIfBlank(
             HttpStatus.INTERNAL_SERVER_ERROR.value(),
             HttpCodeConstant.FAILED,
             UserConstant.USER_AUTH_CREATE_FAILED,
             userService.createUser(dto));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public HttpResult<UserVo> getInfo(@PathVariable Long id) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             UserConstant.USER_UNACCESSIBLE,
             userService.getUserVoById(id));
    }

    @Operation(summary = "获取当前登录账号信息")
    @GetMapping
    public HttpResult<UserVo> getSelf() throws Exception {
        return HttpResult.failedIfBlank(HttpCodeConstant.FAILED,
                UserConstant.USER_UNACCESSIBLE,
                userService.getUserVoById(ServiceUtil.loginUserOrE()));

    }

    @Operation(summary = "根据用户名查找用户")
    @GetMapping("/n/{name}")
    public HttpResult<List<UserVo>> getInfoByName(@PathVariable @Size(min = 1, max = 20) String name) throws Exception {
        return HttpResult.success(userService.getUserVosByName(name));
    }

    @Operation(summary = "修改")
    @PutMapping
    public HttpResult<UserVo> update(@RequestBody @Valid UserUpdateDto dto) throws Exception {

        return HttpResult.failedIfBlank(
             HttpCodeConstant.UPDATE_FAILED,
             UserConstant.USER_UPDATE_FAILED,
             userService.updateAuthedUser(dto));
    }

    @Operation(summary = "关注")
    @PutMapping("/subscribe")
    public HttpResult<?> subscribe(@RequestBody @Valid UserSubDto dto) throws Exception {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             UserConstant.SUBSCRIBE_FAILED,
             userService.subsribe(dto));
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/subscribe")
    public HttpResult<?> unSubscribe(@RequestBody @Valid UserSubDto dto) throws Exception {
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             UserConstant.UNSUBSCRIBE_FAILED,
             userService.unsubscribe(dto));
    }

    @Operation(summary = "获取关注列表")
    @PostMapping("/subscribe")
    public HttpResult<PageResult<UserBriefVo>> getSubscribe(@RequestBody UserSubDto dto) throws Exception {
        if (BeanUtil.isBlank(dto.getFromUser())) {
            dto.setFromUser(ServiceUtil.loginUserOrE());
        }
        return HttpResult.success(userService.getSubscribedUsers(dto));
    }

    @Operation(summary = "删除账号")
    @DeleteMapping
    public HttpResult<?> deleteAccount() throws Exception {
        // TODO 删除账号设置缓冲期
        return HttpResult.onCondition(
             HttpCodeConstant.FAILED,
             UserConstant.DELETE_FAILED,
             userService.deleteAccount(ServiceUtil.loginUserOrE()));
    }

}

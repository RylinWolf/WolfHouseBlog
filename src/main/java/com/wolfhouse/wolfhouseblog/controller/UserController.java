package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.UserConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.JwtUtil;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserLoginDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserLoginVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserRegisterVo;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserAuthService authService;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "登陆")
    @PostMapping("/login")
    public ResponseEntity<HttpResult<UserLoginVo>> login(@RequestBody UserLoginDto dto) {
        try {
            Authentication auth = authManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                    dto.getAccount(),
                    dto.getPassword()));
            log.info("用户[{}]登陆，状态: {}", auth.getPrincipal(), auth.isAuthenticated());
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
    public ResponseEntity<HttpResult<UserRegisterVo>> register(@RequestBody @Valid UserRegisterDto dto) {
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
        return HttpResult.ok(userService.createUser(dto), null);
    }


}

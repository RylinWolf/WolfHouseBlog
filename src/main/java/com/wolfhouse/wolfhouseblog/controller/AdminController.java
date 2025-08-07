package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/api/a")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;

    @PostMapping
    public HttpResult<?> postAdmin(@RequestBody AdminPostDto dto) throws Exception {
        return HttpResult.onCondition(
                HttpCodeConstant.FAILED,
                AdminConstant.CREATE_FAILED,
                service.createAdmin(dto));
    }
}

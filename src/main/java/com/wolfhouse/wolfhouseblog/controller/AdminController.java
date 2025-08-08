package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.AdminConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminPostDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.AdminUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.AdminVo;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/api/a")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;

    @GetMapping("/au")
    public HttpResult<List<Long>> getAuthorities() throws Exception {
        return HttpResult.success(service.getAuthoritiesIds(ServiceUtil.loginUserOrE()));
    }

    @PostMapping
    public HttpResult<AdminVo> postAdmin(@RequestBody AdminPostDto dto) throws Exception {
        return HttpResult.failedIfBlank(
                HttpCodeConstant.FAILED,
                AdminConstant.CREATE_FAILED,
                service.createAdmin(dto));
    }

    @PutMapping
    public HttpResult<AdminVo> updateAdmin(@RequestBody AdminUpdateDto dto) throws Exception {
        return HttpResult.failedIfBlank(
                HttpCodeConstant.UPDATE_FAILED,
                AdminConstant.UPDATE_FAILED,
                service.updateAdmin(dto));
    }
}

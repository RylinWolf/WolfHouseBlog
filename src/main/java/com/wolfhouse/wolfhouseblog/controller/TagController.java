package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.TagConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.vo.TagVo;
import com.wolfhouse.wolfhouseblog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linexsong
 */
@RestController
@Tag(name = "常用标签接口")
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {
    private final TagService service;

    @GetMapping
    @Operation(summary = "获取常用标签列表")
    public HttpResult<List<TagVo>> getTags() throws Exception {
        return HttpResult.success(service.getTagVos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取常用标签")
    public HttpResult<TagVo> getTag(@PathVariable Long id) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             TagConstant.NOT_EXIST,
             service.getTagById(id));
    }

}

package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpMediaTypeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping(value = "/query", consumes = {HttpMediaTypeConstant.APPLICATION_JSON_NULLABLE_VALUE})
    public HttpResult<PageResult<ArticleBriefVo>> query(@RequestBody ArticleQueryPageDto dto) {
        return HttpResult.success(articleService.getBriefQuery(dto));
    }

    @GetMapping("/{id}")
    public HttpResult<ArticleVo> get(@PathVariable Long id) throws Exception {
        return HttpResult.failedIfBlank(
                HttpCodeConstant.ACCESS_DENIED,
                ArticleConstant.ACCESS_DENIED,
                articleService.getById(id));
    }

    @PostMapping
    public HttpResult<ArticleVo> post(@RequestBody ArticleDto dto) throws Exception {
        return HttpResult.failedIfBlank(
                HttpCodeConstant.POST_FAILED,
                ArticleConstant.POST_FAILED,
                articleService.post(dto));
    }

    @PutMapping
    public HttpResult<ArticleVo> update(@RequestBody ArticleUpdateDto dto) throws Exception {
        return HttpResult.failedIfBlank(
                HttpCodeConstant.UPDATE_FAILED,
                ArticleConstant.UPDATE_FAILED,
                articleService.update(dto));
    }

    @DeleteMapping("/{id}")
    public HttpResult<?> delete(@PathVariable Long id) throws Exception {
        return HttpResult.onCondition(
                HttpCodeConstant.FAILED, ArticleConstant.DELETE_FAILED,
                articleService.deleteById(id));
    }
}

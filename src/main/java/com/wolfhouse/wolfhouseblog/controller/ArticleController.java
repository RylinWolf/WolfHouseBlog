package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpMediaTypeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public HttpResult<ArticleVo> post(@RequestBody ArticleDto dto) throws Exception {
        return HttpResult.failedIfBlank(
                HttpCodeConstant.POST_FAILED,
                ArticleConstant.POST_FAILED,
                articleService.post(dto));
    }
}

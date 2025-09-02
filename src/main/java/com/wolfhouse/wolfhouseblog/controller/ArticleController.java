package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpMediaTypeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.*;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleCommentVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleActionService;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author rylinwolf
 */
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
@Tag(name = "文章接口")
public class ArticleController {
    private final ArticleService articleService;
    private final ArticleActionService actionService;

    @Operation(summary = "文章检索")
    @PostMapping(value = "/query", consumes = {HttpMediaTypeConstant.APPLICATION_JSON_NULLABLE_VALUE})
    public HttpResult<PageResult<ArticleBriefVo>> query(@RequestBody ArticleQueryPageDto dto) throws Exception {
        return HttpResult.success(articleService.getBriefQuery(dto));
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public HttpResult<ArticleVo> get(@PathVariable Long id) throws Exception {
        return HttpResult.failedIfBlank(
            HttpCodeConstant.ACCESS_DENIED,
            ArticleConstant.ACCESS_DENIED,
            articleService.getVoById(id));
    }

    @Operation(summary = "发布")
    @PostMapping
    public HttpResult<ArticleVo> post(@RequestBody @Valid ArticleDto dto) throws Exception {
        return HttpResult.failedIfBlank(
            HttpCodeConstant.POST_FAILED,
            ArticleConstant.POST_FAILED,
            articleService.post(dto));
    }

    @Operation(summary = "暂存")
    @PostMapping("/draft")
    public HttpResult<ArticleVo> draft(@RequestBody ArticleDraftDto dto) throws Exception {
        return HttpResult.failedIfBlank(
            HttpCodeConstant.FAILED,
            ArticleConstant.DRAFT_FAILED,
            articleService.draft(dto));
    }

    @Operation(summary = "更新")
    @PatchMapping
    public HttpResult<ArticleVo> update(@RequestBody ArticleUpdateDto dto) throws Exception {
        return HttpResult.failedIfBlank(
            HttpCodeConstant.UPDATE_FAILED,
            ArticleConstant.UPDATE_FAILED,
            articleService.update(dto));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public HttpResult<?> delete(@PathVariable Long id) throws Exception {
        return HttpResult.onCondition(
            HttpCodeConstant.FAILED, ArticleConstant.DELETE_FAILED,
            articleService.deleteById(id));
    }

    @Operation(summary = "获取评论")
    @PostMapping(value = "/comment", consumes = {HttpMediaTypeConstant.APPLICATION_JSON_NULLABLE_VALUE})
    public HttpResult<PageResult<ArticleCommentVo>> getComments(
        @RequestBody ArticleCommentQueryDto dto) throws Exception {
        return HttpResult.success(actionService.getArticleCommentVos(dto));
    }

    @Operation(summary = "发布评论")
    @PostMapping(value = "/comment/post")
    public HttpResult<PageResult<ArticleCommentVo>> postComment(@RequestBody ArticleCommentDto dto) throws Exception {
        return HttpResult.failedIfBlank(
            HttpCodeConstant.FAILED,
            ArticleConstant.COMMENT_FAILED,
            actionService.postComment(dto));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment")
    public HttpResult<PageResult<ArticleCommentVo>> deleteComment(
        @RequestBody ArticleCommentDeleteDto dto) throws Exception {
        return HttpResult.success(actionService.deleteComment(dto));
    }

    @Operation(summary = "查询是否点赞")
    @GetMapping("/like/{id}")
    public HttpResult<Boolean> isLiked(@PathVariable Long id) throws Exception {
        return HttpResult.success(actionService.isLiked(id));
    }

    @Operation(summary = "点赞")
    @PostMapping("/like/{id}")
    public HttpResult<?> like(@PathVariable Long id) throws Exception {
        return HttpResult.onCondition(
            HttpCodeConstant.FAILED,
            ArticleConstant.LIKE_FAILED,
            actionService.like(id));
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/unlike/{id}")
    public HttpResult<?> unLike(@PathVariable Long id) throws Exception {
        return HttpResult.onCondition(
            HttpCodeConstant.FAILED,
            ArticleConstant.UNLIKE_FAILED,
            actionService.unlike(id));
    }
}

package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.FavoritesConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.FavoritesVo;
import com.wolfhouse.wolfhouseblog.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rylinwolf
 */
@RestController("/favorites")
@Slf4j
@Tag(name = "收藏夹接口")
@RequiredArgsConstructor
public class FavoritesController {
    private final FavoritesService service;

    @GetMapping("/{userId}")
    @Operation(summary = "获取指定用户收藏夹")
    public HttpResult<List<FavoritesVo>> getFavorites(@PathVariable Long userId) {
        return HttpResult.success(service.getFavoritesList(userId));
    }

    @DeleteMapping("/{favoritesId}")
    @Operation(summary = "删除指定收藏夹")
    public HttpResult<List<FavoritesVo>> deleteFavorites(@PathVariable Long favoritesId) throws Exception {
        return HttpResult.success(service.deleteFavorites(favoritesId));
    }

    @PostMapping
    @Operation(summary = "新建收藏夹")
    public HttpResult<List<FavoritesVo>> addFavorites(@RequestBody FavoritesDto dto) throws Exception {
        return HttpResult.failedIfBlank(
            HttpCodeConstant.POST_FAILED,
            FavoritesConstant.ADD_FAILED,
            service.addFavorites(dto));
    }

    @PatchMapping
    @Operation(summary = "修改收藏夹")
    public HttpResult<FavoritesVo> updateFavorites(@RequestBody FavoritesUpdateDto dto) throws Exception {
        return HttpResult.failedIfBlank(
            HttpCodeConstant.UPDATE_FAILED,
            FavoritesConstant.UPDATE_FAILED,
            service.updateFavorites(dto));
    }

}

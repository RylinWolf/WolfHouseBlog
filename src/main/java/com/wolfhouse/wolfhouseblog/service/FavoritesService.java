package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Favorites;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.FavoritesVo;

import java.util.List;

/**
 * 收藏夹服务接口
 * 提供用户收藏夹的相关操作，包括查询、添加和删除
 *
 * @author rylinwolf
 */
public interface FavoritesService extends IService<Favorites> {
    /**
     * 获取用户的收藏夹列表
     *
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    List<FavoritesVo> getFavoritesList(Long userId);

    /**
     * 添加新的收藏夹
     *
     * @param dto 收藏夹数据传输对象，包含用户ID和收藏夹标题
     * @return 更新后的收藏夹列表
     */
    List<FavoritesVo> addFavorites(FavoritesDto dto);

    /**
     * 删除指定的收藏夹
     *
     * @param dto 收藏夹数据传输对象，包含要删除的收藏夹信息
     * @return 更新后的收藏夹列表
     */
    List<FavoritesVo> deleteFavorites(FavoritesDto dto);

}

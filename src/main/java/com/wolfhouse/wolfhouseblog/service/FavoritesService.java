package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Favorites;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.FavoritesVo;

import java.util.List;

/**
 * 收藏夹服务接口
 * 提供用户收藏夹的相关操作，包括查询、添加、修改和删除。
 * 支持收藏夹的公开和私密设置，以及默认收藏夹的特殊处理。
 *
 * @author rylinwolf
 */
public interface FavoritesService extends IService<Favorites> {
    /**
     * 获取用户的收藏夹列表。
     * 如果查询的是其他用户的收藏夹，则只返回公开的收藏夹。
     * 如果查询自己的收藏夹，则返回所有收藏夹。
     *
     * @param userId 要查询的用户ID
     * @return 收藏夹列表，包含收藏夹的基本信息
     * @throws Exception 未登录或验证失败
     */
    List<FavoritesVo> getFavoritesList(Long userId) throws Exception;

    /**
     * 添加新的收藏夹。
     * 会验证收藏夹标题是否重复，以及用户是否已登录。
     *
     * @param dto 收藏夹数据传输对象，包含收藏夹标题和可见性设置
     * @return 更新后的收藏夹列表
     * @throws Exception 当用户未登录或标题重复时抛出异常
     */
    List<FavoritesVo> addFavorites(FavoritesDto dto) throws Exception;

    /**
     * 删除指定的收藏夹。
     * 会验证用户是否拥有该收藏夹的删除权限。
     *
     * @param favoritesId 要删除的收藏夹ID
     * @return 删除后的收藏夹列表
     * @throws Exception 当用户未登录或无权删除时抛出异常
     */
    List<FavoritesVo> deleteFavorites(Long favoritesId) throws Exception;

    /**
     * 根据ID获取收藏夹详细信息。
     * 返回收藏夹的完整视图对象，包含标题、可见性等信息。
     *
     * @param favoritesId 收藏夹ID
     * @return 收藏夹视图对象
     * @throws Exception 当收藏夹不存在时抛出异常
     */
    FavoritesVo getFavoritesVoById(Long favoritesId) throws Exception;

    /**
     * 获取默认收藏夹的视图对象。
     * 默认收藏夹是用户的特殊收藏夹，用于默认存储用户的内容。
     *
     * @return 默认收藏夹视图对象，包含收藏夹的基本信息
     * @throws Exception 当用户未登录或发生其他异常时抛出
     */
    FavoritesVo getDefaultFavoritesVo() throws Exception;

    /**
     * 更新收藏夹信息。
     * 支持更新收藏夹的标题和可见性设置，会进行相应的权限验证。
     *
     * @param dto 收藏夹更新数据传输对象，包含要更新的字段
     * @return 更新后的收藏夹视图对象
     * @throws Exception 当用户未登录、无权限或更新失败时抛出异常
     */
    FavoritesVo updateFavorites(FavoritesUpdateDto dto) throws Exception;

    /**
     * 检查当前登录用户是否已存在指定标题的收藏夹。
     * 用于创建收藏夹时的标题重复检查。
     *
     * @param title 待检查的收藏夹标题
     * @return true表示该标题已存在，false表示不存在
     * @throws Exception 当用户未登录时抛出异常
     */
    Boolean isFavoritesTitleExist(String title) throws Exception;

    /**
     * 检查指定的收藏夹ID是否属于当前登录用户。
     * 用于验证用户对收藏夹的操作权限。
     *
     * @param favoritesId 待检查的收藏夹ID
     * @return true表示属于当前用户，false表示不属于
     * @throws Exception 当用户未登录时抛出异常
     */
    Boolean isFavoritesIdOwn(Long favoritesId) throws Exception;
}

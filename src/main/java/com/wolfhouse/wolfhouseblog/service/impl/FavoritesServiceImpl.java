package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.FavoritesConstant;
import com.wolfhouse.wolfhouseblog.common.enums.DefaultEnum;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.favorites.FavoritesVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.FavoritesMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Favorites;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.FavoritesVo;
import com.wolfhouse.wolfhouseblog.service.FavoritesService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.FavoritesTableDef.FAVORITES;

/**
 * @author rylinwolf
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {
    private final ServiceAuthMediator mediator;

    @PostConstruct
    private void init() {
        this.mediator.registerFavorite(this);
    }

    @Override
    public List<FavoritesVo> getFavoritesList(Long userId) {
        // 初始化登录用户
        Long login = null;
        try {
            login = mediator.loginUserOrE();
        } catch (Exception ignored) {}

        return mapper.selectListByQueryAs(
            QueryWrapper.create()
                        .where(FAVORITES.USER_ID.eq(userId))
                        // 若查询用户不为登录用户，则仅查询公开权限
                        .and(FAVORITES.VISIBILITY.eq(VisibilityEnum.PUBLIC, !userId.equals(login))),
            FavoritesVo.class);
    }

    @Override
    public List<FavoritesVo> addFavorites(FavoritesDto dto) throws Exception {
        Long login = mediator.loginUserOrE();

        Favorites favorites = BeanUtil.copyProperties(dto, Favorites.class);
        VerifyTool.of(
                      // 名称验证
                      FavoritesVerifyNode.title(mediator)
                                         .target(favorites.getTitle()))
                  .doVerify();

        int i = mapper.insert(favorites);
        if (i == 1) {
            return getFavoritesList(login);
        }
        // 新增失败
        log.error("新增收藏夹失败: {}, {}", login, favorites);
        throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
    }

    @Override
    public List<FavoritesVo> deleteFavorites(Long favoritesId) throws Exception {
        Long login = mediator.loginUserOrE();
        // 验证是否拥有指定收藏夹
        VerifyTool.of(
                      FavoritesVerifyNode.id(mediator)
                                         .target(favoritesId),
                      // 不得删除默认收藏夹
                      new BaseVerifyNode<Long>() {
                          {
                              customException = new ServiceException(FavoritesConstant.IS_DEFAULT);
                          }

                          @Override
                          public boolean verify() {
                              return getById(favoritesId).getIsDefault()
                                                         .equals(DefaultEnum.NOT_DEFAULT);
                          }
                      })
                  .doVerify();
        int i = mapper.deleteById(favoritesId);
        if (i == 1) {
            // TODO 通知文章交互服务，转移内容到默认收藏夹
            return getFavoritesList(login);
        }
        log.error("删除收藏夹失败: {}, {}", login, favoritesId);
        throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
    }

    @Override
    public Boolean isFavoritesTitleExist(String title) throws Exception {
        Long login = mediator.loginUserOrE();
        return exists(QueryWrapper.create()
                                  .where(FAVORITES.USER_ID.eq(login))
                                  .and(FAVORITES.TITLE.eq(title)));
    }

    @Override
    public Boolean isFavoritesIdOwn(Long favoritesId) throws Exception {
        Long login = mediator.loginUserOrE();
        return exists(QueryWrapper.create()
                                  .where(FAVORITES.USER_ID.eq(login))
                                  .and(FAVORITES.ID.eq(favoritesId)));
    }
}

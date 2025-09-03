package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
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
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.FavoritesMapper;
import com.wolfhouse.wolfhouseblog.mq.service.MqArticleService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Favorites;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqFavoritesRemoveDto;
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
    private final MqArticleService mqArticleService;

    @PostConstruct
    private void init() {
        this.mediator.registerFavorite(this);
    }

    @Override
    public List<FavoritesVo> getFavoritesList(Long userId) throws Exception {
        // 验证要获取收藏夹的用户 ID 是否可达
        VerifyTool.of(
                      UserVerifyNode.id(mediator)
                                    .target(userId))
                  .doVerify();
        // 初始化登录用户
        Long login = mediator.loginUserOrNull();

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
                      FavoritesVerifyNode.idOwn(mediator)
                                         .target(favoritesId),
                      // 不得删除默认收藏夹
                      EmptyVerifyNode.of(favoritesId)
                                     .setCustomException(new ServiceException(FavoritesConstant.IS_DEFAULT))
                                     .predicate(t -> getById(t).getIsDefault()
                                                               .equals(DefaultEnum.NOT_DEFAULT)))
                  .doVerify();
        int i = mapper.deleteById(favoritesId);
        if (i == 1) {
            // 通知文章交互服务清空收藏夹
            var mqDto = new MqFavoritesRemoveDto(favoritesId);
            mqDto.setLoginId(login);

            mqArticleService.articleFavoritesRemove(mqDto);
            return getFavoritesList(login);
        }
        log.error("删除收藏夹失败: {}, {}", login, favoritesId);
        throw new ServiceException(ServiceExceptionConstant.SERVICE_ERROR);
    }

    @Override
    public FavoritesVo getFavoritesVoById(Long favoritesId) throws Exception {
        return mapper.selectOneByQueryAs(
            QueryWrapper.create()
                        // 指定收藏夹 ID
                        .where(FAVORITES.ID.eq(favoritesId))
                        // 若非自己的，则只允许获取公开可见收藏夹
                        .and(FAVORITES.VISIBILITY.eq(VisibilityEnum.PUBLIC, !isFavoritesIdOwn(favoritesId))),
            FavoritesVo.class);
    }

    @Override
    public FavoritesVo updateFavorites(FavoritesUpdateDto dto) throws Exception {
        Long id = dto.getId();
        Long login = mediator.loginUserOrE();
        VerifyTool.of(
                      // 校验 ID、收藏夹标题
                      FavoritesVerifyNode.idOwn(mediator)
                                         .target(id),
                      FavoritesVerifyNode.title(mediator)
                                         .target(dto.getTitle()
                                                    .orElse(null))
                                         .allowNull(true))
                  .doVerify();

        // 执行更新
        var chain = UpdateChain.of(FAVORITES)
                               .where(FAVORITES.ID.eq(id));

        // 更新标题
        dto.getTitle()
           .ifPresent(t -> chain.set(FAVORITES.TITLE, t, t != null));

        // 更新权限
        dto.getVisibility()
           .ifPresent(v -> chain.set(FAVORITES.VISIBILITY, v, v != null));

        // 更新默认
        dto.getIsDefault()
           .ifPresent(v -> {
               // 默认字段不为 true
               if (v == null || v.equals(DefaultEnum.NOT_DEFAULT)) {
                   return;
               }
               // 取消默认
               UpdateChain.of(FAVORITES)
                          .where(FAVORITES.USER_ID.eq(login))
                          .and(FAVORITES.IS_DEFAULT.eq(DefaultEnum.DEFAULT))
                          .set(FAVORITES.IS_DEFAULT, DefaultEnum.NOT_DEFAULT)
                          .update();
               // 设置当前为默认
               chain.set(FAVORITES.IS_DEFAULT, v);
           });

        if (!chain.update()) {
            throw new ServiceException(FavoritesConstant.UPDATE_FAILED);
        }
        return getFavoritesVoById(id);
    }

    @Override
    public Boolean isFavoritesTitleExist(String title) throws Exception {
        Long login = mediator.loginUserOrNull();
        if (login == null) {
            return false;
        }
        return exists(QueryWrapper.create()
                                  .where(FAVORITES.USER_ID.eq(login))
                                  .and(FAVORITES.TITLE.eq(title)));
    }

    @Override
    public Boolean isFavoritesIdOwn(Long favoritesId) throws Exception {
        Long login = mediator.loginUserOrNull();
        if (login == null) {
            return false;
        }
        return exists(QueryWrapper.create()
                                  .where(FAVORITES.USER_ID.eq(login))
                                  .and(FAVORITES.ID.eq(favoritesId)));
    }
}

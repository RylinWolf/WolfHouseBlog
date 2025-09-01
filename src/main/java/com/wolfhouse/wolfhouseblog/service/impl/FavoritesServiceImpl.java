package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.ServiceExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
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
    public List<FavoritesVo> addFavorites(FavoritesDto dto) {
        return List.of();
    }

    @Override
    public List<FavoritesVo> deleteFavorites(FavoritesDto dto) {
        return List.of();
    }

    @Override
    public Boolean isFavoritesTitleExist(String title) {
        return null;
    }
}

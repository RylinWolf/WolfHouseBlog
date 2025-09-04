package com.wolfhouse.wolfhouseblog.mq.listener;

import com.wolfhouse.wolfhouseblog.common.enums.DefaultEnum;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.mq.MqTools;
import com.wolfhouse.wolfhouseblog.pojo.dto.FavoritesDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqAuthDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.FavoritesVo;
import com.wolfhouse.wolfhouseblog.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FavoritesListener {
    private final MqTools mqTools;
    private final FavoritesService service;


    @Transactional(rollbackFor = Exception.class)
    @RabbitListener
    public void initFavorites(MqAuthDto dto) throws Exception {
        mqTools.setLoginAuth(dto);
        FavoritesDto defaultFavorites = new FavoritesDto("默认收藏夹",
                                                         VisibilityEnum.PUBLIC,
                                                         DefaultEnum.DEFAULT);
        List<FavoritesVo> favorites = service.addFavorites(defaultFavorites);
        if (favorites.size() != 1) {
            log.error("添加收藏夹结果错误: {}, favorites: {}", dto.getLoginId(), favorites);
            throw new AmqpRejectAndDontRequeueException(dto.getLoginId() + ": 当前无法添加默认收藏夹");
        }
    }
}

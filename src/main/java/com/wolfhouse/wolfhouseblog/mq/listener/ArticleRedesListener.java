package com.wolfhouse.wolfhouseblog.mq.listener;

import com.wolfhouse.wolfhouseblog.application.ArticleApplicationService;
import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleEsConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.es.ArticleEsDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.redis.ArticleRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 文章 Redis、ES 监听器
 *
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleRedesListener {
    private final ArticleElasticServiceImpl esService;
    private final ArticleRedisService redisService;
    private final ArticleApplicationService applicationService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.POST_QUEUE),
        exchange = @Exchange(
            name = MqArticleEsConstant.POST_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqArticleEsConstant.POST_KEY}
    ))
    public void post(ArticleVo vo) throws InterruptedException {
        log.debug("监听到发布文章信息: {}", vo.getId());
        esService.saveOne(BeanUtil.copyProperties(vo, ArticleEsDto.class));
        redisService.cacheOrUpdateArticle(vo);
        log.debug("{} 文章发布完成", vo.getId());
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.UPDATE_QUEUE),
        exchange = @Exchange(
            name = MqArticleEsConstant.UPDATE_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqArticleEsConstant.UPDATE_KEY}
    ))
    public void update(ArticleUpdateDto dto) {
        try {
            log.debug("监听到更新文章信息: {}", dto.getId());
            ArticleVo update = esService.update(dto);
            if (update == null) {
                throw new ServiceException(ArticleConstant.UPDATE_FAILED + dto.getId());
            }
            log.debug("{} 文章更新完成", update.getId());
            log.debug("更新文章缓存: {}", dto.getId());
            // 移除缓存
            redisService.removeArticleCache(update.getId());
            // 重新获取 Vo 并保存至缓存
            update = applicationService.getArtVoSync(update.getId());
            log.debug("{} 文章缓存更新完成", update.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.DELETE_QUEUE),
        exchange = @Exchange(
            name = MqArticleEsConstant.DELETE_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqArticleEsConstant.DELETE_KEY}
    ))
    public void delete(Long id) {
        try {
            log.debug("监听到删除文章信息: {}", id);
            esService.deleteById(id);
            log.debug("{} 文章删除成功", id);
            log.debug("移除文章缓存: {}", id);
            redisService.removeArticleCache(id);
            log.debug("{} 文章缓存已移除", id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.LIKE_QUEUE),
        exchange = @Exchange(name = MqArticleEsConstant.LIKE_EXCHANGE,
                             type = ExchangeTypes.TOPIC),
        key = {MqArticleEsConstant.LIKE_KEY}
    ))
    public void like(Long id) {
        log.debug("监听到文章点赞: {}", id);
        if (esService.addLikes(id, 1L)) {
            log.debug("{} 文章点赞成功", id);
            return;
        }
        log.error("文章点赞失败: {}", id);
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.UNLIKE_QUEUE),
        exchange = @Exchange(name = MqArticleEsConstant.UNLIKE_EXCHANGE,
                             type = ExchangeTypes.TOPIC),
        key = {MqArticleEsConstant.UNLIKE_KEY}
    ))
    public void unlike(Long id) {
        log.debug("监听到文章取消点赞: {}", id);
        if (esService.addLikes(id, -1L)) {
            log.debug("{} 文章点赞取消成功", id);
            return;
        }
        log.error("文章点赞取消失败: {}", id);
    }
}

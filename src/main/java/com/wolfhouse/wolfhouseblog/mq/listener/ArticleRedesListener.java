package com.wolfhouse.wolfhouseblog.mq.listener;

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
    private final ArticleElasticServiceImpl articleService;
    private final ArticleRedisService redisService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = MqArticleEsConstant.POST_QUEUE),
        exchange = @Exchange(
            name = MqArticleEsConstant.POST_EXCHANGE,
            type = ExchangeTypes.TOPIC
        ),
        key = {MqArticleEsConstant.POST_KEY}
    ))
    public void post(ArticleVo vo) {
        log.debug("监听到发布文章信息: {}", vo.getId());
        articleService.saveOne(BeanUtil.copyProperties(vo, ArticleEsDto.class));
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
            ArticleVo update = articleService.update(dto);
            if (update == null) {
                throw new ServiceException(ArticleConstant.UPDATE_FAILED + dto.getId());
            }
            log.debug("{} 文章更新完成", dto.getId());
            log.debug("更新文章缓存: {}", dto.getId());
            redisService.removeArticleCache(dto.getId());
            redisService.cacheArticle(update);
            log.debug("{} 文章缓存更新完成", dto.getId());
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
            articleService.deleteById(id);
            log.debug("{} 文章删除成功", id);
            log.debug("移除文章缓存: {}", id);
            redisService.removeArticleCache(id);
            log.debug("{} 文章缓存已移除", id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }
}

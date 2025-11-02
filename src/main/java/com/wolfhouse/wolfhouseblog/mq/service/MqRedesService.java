package com.wolfhouse.wolfhouseblog.mq.service;

import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleEsConstant;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqRedesService {
    private final RabbitTemplate template;

    /**
     * 发布文章
     * 将文章对象发送到消息队列用于ES索引创建
     *
     * @param article 文章对象
     */
    public void postArticle(Article article) {
        log.debug("发布文章: {}", article.getId());
        template.convertAndSend(MqArticleEsConstant.POST_EXCHANGE, MqArticleEsConstant.POST_KEY, article);
    }

    /**
     * 更新文章
     * 将文章对象发送到消息队列用于ES索引更新
     *
     * @param dto 文章更新对象
     */
    public void updateArticle(ArticleUpdateDto dto) {
        log.debug("更新文章: {}", dto.getId());
        template.convertAndSend(MqArticleEsConstant.UPDATE_EXCHANGE, MqArticleEsConstant.UPDATE_KEY, dto);
    }

    /**
     * 删除文章
     * 将文章ID发送到消息队列用于ES索引删除
     *
     * @param id 文章ID
     */
    public void deleteArticle(Long id) {
        log.debug("删除文章: {}", id);
        template.convertAndSend(MqArticleEsConstant.DELETE_EXCHANGE, MqArticleEsConstant.DELETE_KEY, id);
    }

    /**
     * 点赞文章
     * 将文章ID发送到消息队列，用于处理文章点赞操作
     *
     * @param id 文章ID
     */
    public void like(Long id) {
        log.debug("点赞文章: {}", id);
        template.convertAndSend(MqArticleEsConstant.LIKE_EXCHANGE, MqArticleEsConstant.LIKE_KEY, id);
    }

    public void unlike(Long id) {
        log.debug("取消点赞文章: {}", id);
        template.convertAndSend(MqArticleEsConstant.LIKE_EXCHANGE, MqArticleEsConstant.UNLIKE_KEY, id);
    }
}

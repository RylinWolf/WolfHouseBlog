package com.wolfhouse.wolfhouseblog.mq.listener;

import com.mybatisflex.core.update.UpdateChain;
import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleConstant;
import com.wolfhouse.wolfhouseblog.mapper.ArticleMapper;
import com.wolfhouse.wolfhouseblog.mq.MqTools;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqArticleTagRemoveDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqPartitionChangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;


/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleListener {
    private final MqTools mqTools;
    private final ArticleMapper mapper;

    @RabbitListener(
         bindings = @QueueBinding(
              value = @Queue(name = MqArticleConstant.PARTITION_CHANGE_QUEUE),
              exchange = @Exchange(
                   name = MqArticleConstant.PARTITION_CHANGE_EXCHANGE,
                   type = ExchangeTypes.TOPIC
              ),
              key = {MqArticleConstant.KEY_PARTITION_CHANGE}
         ))
    public void partitionChangeListener(MqPartitionChangeDto dto) {
        log.info("监听到文章分区修改: {}", dto);

        boolean update = UpdateChain.of(Article.class)
                                    .where(ARTICLE.AUTHOR_ID.eq(dto.getUserId())
                                                            .and(ARTICLE.PARTITION_ID.in(dto.getOldIds())))
                                    .set(ARTICLE.PARTITION_ID, dto.getNewId())
                                    .update();
        if (!update) {
            log.warn("没有文章的分区进行修改");
        }
    }

    @RabbitListener(
         bindings = @QueueBinding(
              value = @Queue(name = MqArticleConstant.TAG_REMOVE_QUEUE),
              exchange = @Exchange(
                   name = MqArticleConstant.TAG_REMOVE_EXCHANGE,
                   type = ExchangeTypes.TOPIC
              ),
              key = {MqArticleConstant.KEY_TAG_REMOVE}
         ))
    public void comUseTagsRemoveListener(MqArticleTagRemoveDto dto) throws Exception {
        log.info("监听到移除常用标签: {}", dto);
        mqTools.setLoginAuth(dto);
        System.out.println(dto.getTagIds());
        if (mapper.removeTags(dto.getUserId(), dto.getTagIds()) == 0) {
            log.warn("没有文章的标签被修改");
        }
    }
}

package com.wolfhouse.wolfhouseblog.mq.listener;

import com.mybatisflex.core.update.UpdateChain;
import com.wolfhouse.wolfhouseblog.common.constant.mq.MqArticleConstant;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
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
}

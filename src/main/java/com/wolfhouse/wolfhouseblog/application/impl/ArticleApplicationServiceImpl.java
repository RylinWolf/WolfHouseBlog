package com.wolfhouse.wolfhouseblog.application.impl;

import com.wolfhouse.wolfhouseblog.application.ArticleApplicationService;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.mq.service.MqArticleService;
import com.wolfhouse.wolfhouseblog.mq.service.MqEsService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.redis.ArticleRedisService;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.UserService;
import com.wolfhouse.wolfhouseblog.service.mediator.ArticleEsDbMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class ArticleApplicationServiceImpl implements ArticleApplicationService {
    private final ArticleService articleService;
    private final ArticleRedisService redisService;
    private final MqArticleService mqArticleService;
    private final MqEsService mqEsService;
    private final ArticleElasticServiceImpl elasticService;
    private final UserService userService;
    private final ArticleEsDbMediator esDbMediator;

    @Override
    public ArticleVo getArticleVoById(Long id) throws Exception {
        // 从缓存中获取
        ArticleVo vo = redisService.getCachedArticle(id);
        if (!BeanUtil.isBlank(vo)) {
            return vo;
        }
        // 从 ES 或数据库中获取
        Article article = esDbMediator.getArticleById(id);
        if (BeanUtil.isBlank(article)) {
            // 无该文章
            return null;
        }

        // 获取作者信息并注入
        vo = BeanUtil.copyProperties(article, ArticleVo.class);
        vo.setAuthor(userService.getUserBriefById(article.getAuthorId()));
        return vo;
    }
}

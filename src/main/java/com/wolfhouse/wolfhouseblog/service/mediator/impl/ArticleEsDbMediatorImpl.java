package com.wolfhouse.wolfhouseblog.service.mediator.impl;

import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.es.ArticleEsDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.mediator.ArticleEsDbMediator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author linexsong
 */
@Component
public class ArticleEsDbMediatorImpl implements ArticleEsDbMediator {
    private ArticleElasticServiceImpl esService;
    private ArticleService articleService;

    @Override
    public void registerArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void registerEsService(ArticleElasticServiceImpl esService) {
        this.esService = esService;
    }

    @Override
    public Set<Long> addViewsRedisToEs(Map<String, Long> articleIdViews) {
        return esService.addViews(articleIdViews);
    }

    @Override
    public Set<Long> addViewsRedisToDb(Map<String, Long> articleIdViews) {
        return articleService.addViews(articleIdViews);
    }

    @Override
    public Set<Long> addViewsRedisToBoth(Map<String, Long> articleIdViews) {
        Set<Long> dbs = addViewsRedisToDb(articleIdViews);
        // 数据库同步失败，则不往下进行
        if (dbs == null || dbs.size() != articleIdViews.size()) {
            return null;
        }
        Set<Long> es = addViewsRedisToEs(articleIdViews);
        dbs.retainAll(es);
        return dbs;
    }

    @Override
    public Boolean addViewsRedisToDb(Long articleId, Long views) {
        return articleService.addViews(articleId, views);
    }

    @Override
    public Boolean addViewsRedisToEs(Long articleId, Long views) {
        return esService.addViews(articleId, views);
    }

    @Override
    public Boolean addViewsRedisToBoth(Long articleId, Long views) {
        return addViewsRedisToDb(articleId, views) && addViewsRedisToEs(articleId, views);
    }

    @Override
    public void syncArticle(Long articleId) throws Exception {
        ArticleVo article = articleService.getVoById(articleId);
        esService.saveOne(BeanUtil.copyProperties(article, ArticleEsDto.class));
    }

    @Override
    public Article getArticleById(Long id) throws Exception {
        // 从 ES 获取 Vo
        Article article = esService.getById(id);
        if (BeanUtil.isBlank(article)) {
            // ES 的文章为空
            if (BeanUtil.isBlank(article = articleService.getById(id))) {
                // 数据库中文章不存在
                return null;
            }
            // 同步文章至 ES
            syncArticle(id);
        }
        return article;
    }
}

package com.wolfhouse.wolfhouseblog.service.mediator.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.es.ArticleEsDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.mediator.ArticleEsDbMediator;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;
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
    public Set<Long> addViewsToEs(Map<String, Long> articleIdViews) {
        return esService.addViews(articleIdViews);
    }

    @Override
    public Set<Long> addViewsToDb(Map<String, Long> articleIdViews) {
        return articleService.addViews(articleIdViews);
    }

    @Override
    public Set<Long> addViewsToEsDb(Map<String, Long> articleIdViews) {
        Set<Long> dbs = addViewsToDb(articleIdViews);
        // 数据库同步失败，则不往下进行
        if (dbs == null || dbs.size() != articleIdViews.size()) {
            return null;
        }
        Set<Long> es = addViewsToEs(articleIdViews);
        dbs.retainAll(es);
        return dbs;
    }

    @Override
    public Boolean addViewsToDb(Long articleId, Long views) {
        return articleService.addViews(articleId, views);
    }

    @Override
    public Boolean addViewsToEs(Long articleId, Long views) {
        return esService.addViews(articleId, views);
    }

    @Override
    public Boolean addViewsToEsDb(Long articleId, Long views) {
        return addViewsToDb(articleId, views) && addViewsToEs(articleId, views);
    }

    @Override
    public void syncArticleFromDb(Long articleId) throws Exception {
        ArticleVo article = articleService.getVoById(articleId);
        esService.saveOne(BeanUtil.copyProperties(article, ArticleEsDto.class));
    }

    @Override
    public void syncArticleFromDb(ArticleEsDto dto) {
        esService.saveOne(dto);
    }

    @Override
    public Article getArticleById(Long id) throws Exception {
        // 从 ES 获取文章
        Article article = esService.getById(id);
        if (BeanUtil.isBlank(article)) {
            // ES 的文章为空
            if (BeanUtil.isBlank(article = articleService.getById(id))) {
                // 数据库中文章不存在
                return null;
            }
            // 同步文章至 ES
            syncArticleFromDb(id);
        }
        return article;
    }

    @Override
    public ArticleVo getArticleVoById(Long id) throws Exception {
        ArticleVo vo = esService.getVoById(id);
        if (BeanUtil.isBlank(vo)) {
            // ES 的 Vo 为空
            vo = articleService.getVoById(id);
            // 保存至 ES
            esService.saveOne(BeanUtil.copyProperties(vo, ArticleEsDto.class));
        }
        return vo;
    }

    @Override
    @Nullable
    public Page<ArticleVo> queryBy(ArticleQueryPageDto dto, QueryColumn[] columns) throws Exception {
        Page<ArticleVo> articlePage = esService.queryVoBy(dto, dto.getHighlight(), columns);
        if (BeanUtil.isBlank(articlePage.getRecords())) {
            PageResult<ArticleVo> vos = articleService.getQueryVo(dto, columns);
            if (vos == null) {
                return null;
            }
            List<ArticleVo> records = vos.getRecords();
            if (records.isEmpty()) {
                return null;
            }
            articlePage = new Page<>(records, vos.getCurrentPage(), records.size(), vos.getTotalRow());
            // 导入 ES 数据
            esService.saveBatchByDefault(BeanUtil.copyList(records, ArticleEsDto.class));
        }
        return articlePage;
    }

    @Override
    public void syncArticleToDb(Long id) throws Exception {
        articleService.getMapper()
                      .insertWithPk(esService.getById(id));
    }

    @Override
    public void syncArticleToDb(ArticleEsDto dto) {
        articleService.getMapper()
                      .insertWithPk(BeanUtil.copyProperties(dto, Article.class));
    }
}

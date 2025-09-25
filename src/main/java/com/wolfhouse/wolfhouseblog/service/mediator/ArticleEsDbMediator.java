package com.wolfhouse.wolfhouseblog.service.mediator;

import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.service.ArticleService;

import java.util.Map;
import java.util.Set;

/**
 * 文章数据中介器接口
 * 用于协调文章在Redis、Elasticsearch和数据库之间的数据同步
 *
 * @author linexsong
 */
public interface ArticleEsDbMediator {

    void registerArticleService(ArticleService articleService);

    void registerEsService(ArticleElasticServiceImpl esService);


    /**
     * 将Redis中的文章浏览量数据同步到Elasticsearch
     *
     * @param articleIdViews 文章ID和对应的浏览量映射
     * @return 成功更新的文章ID集合
     */
    Set<Long> addViewsRedisToEs(Map<String, Long> articleIdViews);

    /**
     * 将Redis中的文章浏览量数据同步到数据库
     *
     * @param articleIdViews 文章ID和对应的浏览量映射
     * @return 成功更新的文章ID集合
     */
    Set<Long> addViewsRedisToDb(Map<String, Long> articleIdViews);

    /**
     * 将Redis中的文章浏览量数据同步到Elasticsearch和数据库
     *
     * @param articleIdViews 文章ID和对应的浏览量映射
     * @return es 与 数据库都成功更新的文章ID集合
     */
    Set<Long> addViewsRedisToBoth(Map<String, Long> articleIdViews);

    /**
     * 将Redis中单个文章的浏览量同步到数据库
     *
     * @param articleId 文章ID
     * @param views     浏览量
     * @return 是否更新成功
     */
    Boolean addViewsRedisToDb(Long articleId, Long views);


    /**
     * 将Redis中单个文章的浏览量同步到Elasticsearch。
     *
     * @param articleId 文章ID
     * @param views     浏览量
     * @return 是否更新成功
     */
    Boolean addViewsRedisToEs(Long articleId, Long views);


    /**
     * 将Redis中单个文章的浏览量同步到Elasticsearch和数据库。
     *
     * @param articleId 文章ID
     * @param views     浏览量
     * @return 是否成功同步，成功返回true，失败返回false
     */
    Boolean addViewsRedisToBoth(Long articleId, Long views);
}

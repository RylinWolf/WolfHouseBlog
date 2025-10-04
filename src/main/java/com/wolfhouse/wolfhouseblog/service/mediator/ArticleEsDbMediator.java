package com.wolfhouse.wolfhouseblog.service.mediator;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.es.ArticleEsDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
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

    /**
     * 注册文章数据库服务
     *
     * @param articleService 文章数据库服务
     */
    void registerArticleService(ArticleService articleService);

    /**
     * 注册文章 ES 服务
     *
     * @param esService 文章 ES 服务
     */
    void registerEsService(ArticleElasticServiceImpl esService);


    /**
     * 将Redis中的文章浏览量数据同步到Elasticsearch
     *
     * @param articleIdViews 文章ID和对应的浏览量映射
     * @return 成功更新的文章ID集合
     */
    Set<Long> addViewsToEs(Map<String, Long> articleIdViews);

    /**
     * 将Redis中的文章浏览量数据同步到数据库
     *
     * @param articleIdViews 文章ID和对应的浏览量映射
     * @return 成功更新的文章ID集合
     */
    Set<Long> addViewsToDb(Map<String, Long> articleIdViews);

    /**
     * 将Redis中的文章浏览量数据同步到Elasticsearch和数据库
     *
     * @param articleIdViews 文章ID和对应的浏览量映射
     * @return es 与 数据库都成功更新的文章ID集合
     */
    Set<Long> addViewsToEsDb(Map<String, Long> articleIdViews);

    /**
     * 将Redis中单个文章的浏览量同步到数据库
     *
     * @param articleId 文章ID
     * @param views     浏览量
     * @return 是否更新成功
     */
    Boolean addViewsToDb(Long articleId, Long views);


    /**
     * 将Redis中单个文章的浏览量同步到Elasticsearch。
     *
     * @param articleId 文章ID
     * @param views     浏览量
     * @return 是否更新成功
     */
    Boolean addViewsToEs(Long articleId, Long views);


    /**
     * 将Redis中单个文章的浏览量同步到Elasticsearch和数据库。
     *
     * @param articleId 文章ID
     * @param views     浏览量
     * @return 是否成功同步，成功返回true，失败返回false
     */
    Boolean addViewsToEsDb(Long articleId, Long views);

    /**
     * 同步指定文章ID的相关数据。
     *
     * @param id 文章的唯一标识符
     * @throws Exception 没有权限获取文章时抛出异常
     */
    void syncArticle(Long id) throws Exception;

    void syncArticle(ArticleEsDto article);

    /**
     * 根据文章ID获取对应的ArticleVo对象。
     *
     * @param id 文章的唯一标识符
     * @return 对应的ArticleVo对象
     * @throws Exception 如果获取文章信息失败时抛出异常
     */
    Article getArticleById(Long id) throws Exception;

    ArticleVo getArticleVoById(Long id) throws Exception;

    Page<ArticleVo> queryBy(ArticleQueryPageDto dto, QueryColumn[] columns) throws Exception;
}

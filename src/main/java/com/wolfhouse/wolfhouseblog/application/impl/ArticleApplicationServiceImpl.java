package com.wolfhouse.wolfhouseblog.application.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.wolfhouse.wolfhouseblog.application.ArticleApplicationService;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.mq.service.MqArticleService;
import com.wolfhouse.wolfhouseblog.mq.service.MqEsService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import com.wolfhouse.wolfhouseblog.redis.ArticleRedisService;
import com.wolfhouse.wolfhouseblog.redis.UserRedisService;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.mediator.ArticleEsDbMediator;
import com.wolfhouse.wolfhouseblog.service.mediator.UserEsDbMediator;
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
    private final UserRedisService userRedisService;
    private final MqArticleService mqArticleService;
    private final MqEsService mqEsService;
    private final ArticleElasticServiceImpl elasticService;
    private final UserEsDbMediator userEsDbMediator;
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
        vo.setAuthor(BeanUtil.copyProperties(userEsDbMediator.getUserVoById(article.getAuthorId()), UserBriefVo.class));

        // 保存文章至缓存
        redisService.cacheArticle(vo);
        return vo;
    }

    private Page<ArticleVo> queryVoBy(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        Page<Article> queryVo = elasticService.queryBy(dto, columns);
        Page<ArticleVo> page = new Page<>(queryVo.getPageNumber(), queryVo.getPageSize(), queryVo.getTotalRow());
        // 为 Vo 注入作者信息
        page.setRecords(
            queryVo.getRecords()
                   .stream()
                   .map(a -> {
                       var vo = BeanUtil.copyProperties(a, ArticleVo.class);
                       Long authorId = a.getAuthorId();
                       try {
                           // 从缓存中获取，若缓存不存在则自动更新缓存
                           UserVo userInfo = userEsDbMediator.getUserVoById(authorId);
                           vo.setAuthor(BeanUtil.copyProperties(userInfo, UserBriefVo.class));
                           return vo;
                       } catch (Exception e) {
                           throw new RuntimeException(e);
                       }
                   })
                   .toList());
        return page;
    }

    @Override
    public PageResult<ArticleVo> queryArticleVo(ArticleQueryPageDto dto) throws Exception {
        return queryArticleVo(dto, new QueryColumn[]{});
    }

    @Override
    public PageResult<ArticleVo> queryArticleVo(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        return PageResult.of(queryVoBy(dto, columns));
    }

    @Override
    public PageResult<ArticleBriefVo> queryArticleBriefVo(ArticleQueryPageDto dto) throws Exception {
        return PageResult.of(queryVoBy(dto), ArticleBriefVo.class);
    }
}

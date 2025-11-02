package com.wolfhouse.wolfhouseblog.application.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.wolfhouse.wolfhouseblog.application.ArticleApplicationService;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.exceptions.UserAuthException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.es.ArticleElasticServiceImpl;
import com.wolfhouse.wolfhouseblog.mq.service.MqArticleService;
import com.wolfhouse.wolfhouseblog.mq.service.MqRedesService;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import com.wolfhouse.wolfhouseblog.redis.ArticleRedisService;
import com.wolfhouse.wolfhouseblog.redis.UserRedisService;
import com.wolfhouse.wolfhouseblog.service.ArticleActionService;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import com.wolfhouse.wolfhouseblog.service.mediator.ArticleEsDbMediator;
import com.wolfhouse.wolfhouseblog.service.mediator.UserEsDbMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

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
    private final MqRedesService mqRedesService;
    private final ArticleElasticServiceImpl elasticService;
    private final UserEsDbMediator userEsDbMediator;
    private final ArticleEsDbMediator esDbMediator;
    private final ArticleActionService actionService;

    @Override
    public ArticleVo getArtVoSync(Long id) throws Exception {
        // 从缓存中获取
        ArticleVo vo = redisService.getCachedArticle(id);
        if (!BeanUtil.isBlank(vo)) {
            return vo;
        }
        // 从 ES 或数据库中获取
        vo = esDbMediator.getArticleVoById(id);
        if (BeanUtil.isBlank(vo)) {
            // 无该文章
            return null;
        }

        // 获取作者信息并注入
        vo.setAuthor(BeanUtil.copyProperties(userEsDbMediator.getUserVoById(vo.getAuthorId()), UserBriefVo.class));

        // 获取点赞信息并注入
        Long likeCount = vo.getLikeCount();
        likeCount += redisService.getLikesAndRemove(vo.getId());
        vo.setLikeCount(likeCount);

        // 保存文章至缓存
        redisService.cacheOrUpdateArticle(vo);
        return vo;
    }

    /**
     * 根据查询 Dto 查询指定的列，返回对应视图对象。
     * 该方法负责执行文章查询并填充作者和点赞等相关信息。
     *
     * @param dto     查询参数封装对象，包含分页和过滤条件
     * @param columns 需要查询的指定列，可选参数
     * @return 包含完整文章信息的分页对象
     * @throws Exception 当查询过程中出现数据库访问错误或其他异常时抛出
     */
    @Nullable
    private Page<ArticleVo> queryVoBy(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        Page<ArticleVo> queryVo = esDbMediator.queryBy(dto, columns);
        if (queryVo == null || BeanUtil.isBlank(queryVo.getRecords())) {
            return null;
        }
        Page<ArticleVo> page = new Page<>(queryVo.getPageNumber(), queryVo.getPageSize(), queryVo.getTotalRow());
        // 为 Vo 注入信息
        page.setRecords(
            queryVo.getRecords()
                   .stream()
                   .map(a -> {
                       // 注入作者信息
                       var vo = BeanUtil.copyProperties(a, ArticleVo.class);
                       Long authorId = a.getAuthorId();
                       try {
                           // 从缓存中获取，若缓存不存在则自动更新缓存
                           UserVo userInfo = userEsDbMediator.getUserVoById(authorId);
                           vo.setAuthor(BeanUtil.copyProperties(userInfo, UserBriefVo.class));
                       } catch (UserAuthException e) {
                           // 作者用户已不存在
                           vo.setAuthor(null);
                       } catch (Exception e) {
                           throw new ServiceException(e);
                       }
                       return vo;
                   })
                   .toList());
        return page;
    }

    @Override
    public PageResult<ArticleVo> queryArticleVo(ArticleQueryPageDto dto) throws Exception {
        Page<ArticleVo> page = queryVoBy(dto);
        if (page == null) {
            return null;
        }
        return PageResult.of(page);
    }

    @Override
    public PageResult<ArticleVo> queryArticleVo(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        Page<ArticleVo> page = queryVoBy(dto, columns);
        if (page == null) {
            return null;
        }
        return PageResult.of(page);
    }

    @Override
    public PageResult<ArticleBriefVo> queryArticleBriefVo(ArticleQueryPageDto dto) throws Exception {
        Page<ArticleVo> page = queryVoBy(dto);
        if (page == null) {
            return null;
        }
        return PageResult.of(page, ArticleBriefVo.class);
    }
}

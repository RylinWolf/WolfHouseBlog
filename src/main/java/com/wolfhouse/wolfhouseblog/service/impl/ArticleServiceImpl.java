package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article.ContentVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article.IdReachableVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article.PrimaryVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article.TitleVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.AuthExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.mapper.ArticleMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;

/**
 * @author linexsong
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns) {
        var wrapper = QueryWrapper.create();
        // 构建查询列
        wrapper.select(columns);
        // 查询当前用户的私人日记和全部公开日记
        wrapper.and(q -> {
            q.eq(Article::getVisibility, VisibilityEnum.PUBLIC);
            var userId = ServiceUtil.loginUser();
            if (userId != null) {
                q.or(q2 -> {
                    q2.eq(Article::getVisibility, VisibilityEnum.PRIVATE)
                      .eq(Article::getAuthorId, userId);
                });
            }
        });
        // 构建查询条件
        wrapper.eq(
                       Article::getId,
                       dto.getId()
                          .orElse(null))
               // 按标题查询
               .like(
                       Article::getTitle,
                       dto.getTitle()
                          .orElse(null));

        // 日期范围查询
        if (BeanUtil.isAnyNotBlank(dto.getPostStart(), dto.getPostEnd())) {
            wrapper.ge(
                    Article::getPostTime,
                    dto.getPostStart()
                       .orElse(null),
                    dto.getPostStart()
                       .isPresent());
            wrapper.le(
                    Article::getPostTime,
                    dto.getPostEnd()
                       .orElse(null),
                    dto.getPostEnd()
                       .isPresent());
        }

        return mapper.paginate(dto.getPageNumber(), dto.getPageSize(), wrapper);
    }

    @Override
    public PageResult<ArticleVo> getQuery(ArticleQueryPageDto dto, QueryColumn... columns) {
        return PageResult.of(queryBy(dto, columns), ArticleVo.class);
    }

    @Override
    public PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) {
        return PageResult.of(queryBy(dto, ArticleConstant.BRIEF_COLUMNS), ArticleBriefVo.class);
    }

    @Override
    public ArticleVo getById(Long id) throws Exception {
        VerifyTool.ofLogin(new IdReachableVerifyNode(id, this).exception(AuthExceptionConstant.ACCESS_DENIED))
                  .doVerify();

        return BeanUtil.copyProperties(mapper.selectOneById(id), ArticleVo.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVo post(ArticleDto dto) throws Exception {
        VerifyTool.of(
                          new TitleVerifyNode(dto.getTitle()).exception(ARTICLE.TITLE.getName()),
                          new ContentVerifyNode(dto.getContent()).exception(ARTICLE.CONTENT.getName()),
                          new PrimaryVerifyNode(dto.getPrimary()).exception(ARTICLE.PRIMARY.getName()))
                  .doVerify();

        Article article = BeanUtil.copyProperties(dto, Article.class);
        article.setAuthorId(ServiceUtil.loginUser());

        mapper.insertWithPkBack(article);
        return getById(article.getId());
    }

}

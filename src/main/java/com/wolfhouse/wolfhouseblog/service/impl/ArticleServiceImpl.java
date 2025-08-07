package com.wolfhouse.wolfhouseblog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyConstant;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyStrategy;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyChain;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article.ArticleVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article.IdReachableVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JsonNullableUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.mapper.ArticleMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;

/**
 * @author linexsong
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource(name = "jsonNullableObjectMapper")
    private ObjectMapper jsonNullableObjectMapper;


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
                          .orElse(null))
               // 按作者查询
               .eq(
                       Article::getAuthorId,
                       dto.getAuthorId()
                          .orElse(null));

        // 日期范围查询
        LocalDateTime start = dto.getPostStart()
                                 .orElse(null);
        LocalDateTime end = dto.getPostEnd()
                               .orElse(null);

        if (BeanUtil.isAnyNotBlank(start, end)) {
            wrapper.ge(Article::getPostTime, start, start != null);
            wrapper.le(Article::getPostTime, end, end != null);
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
        BaseVerifyChain chain = VerifyTool.of(new IdReachableVerifyNode(
                id,
                this).setStrategy(VerifyStrategy.NORMAL));

        return chain.doVerify() ? BeanUtil.copyProperties(mapper.selectOneById(id), ArticleVo.class) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVo post(ArticleDto dto) throws Exception {
        VerifyTool.of(
                          ArticleVerifyNode.TITLE.target(dto.getTitle())
                                                 .exception(ARTICLE.TITLE.getName()),
                          ArticleVerifyNode.CONTENT.target(dto.getContent())
                                                   .exception(ARTICLE.CONTENT.getName()),
                          ArticleVerifyNode.PRIMARY.target(dto.getPrimary())
                                                   .allowNull(true)
                                                   .exception(ARTICLE.PRIMARY.getName()))
                  .doVerify();

        Article article = BeanUtil.copyProperties(dto, Article.class);
        article.setAuthorId(ServiceUtil.loginUser());

        mapper.insertWithPkBack(article);
        return getById(article.getId());
    }

    @Override
    public ArticleVo update(ArticleUpdateDto dto) throws Exception {
        String title = JsonNullableUtil.getObjOrNull(dto.getTitle());
        String content = JsonNullableUtil.getObjOrNull(dto.getContent());
        String primary = JsonNullableUtil.getObjOrNull(dto.getPrimary());

        // TODO 在修改时，检查分区是否存在
        VerifyTool.ofLogin(
                          ArticleVerifyNode.id(dto.getId(), this),
                          ArticleVerifyNode.title(title, true)
                                           .exception(ARTICLE.TITLE.getName()),
                          ArticleVerifyNode.content(content, true)
                                           .exception(ARTICLE.CONTENT.getName()),
                          ArticleVerifyNode.primary(primary, true)
                                           .exception(ARTICLE.PRIMARY.getName()),
                          new NotAllBlankVerifyNode(
                                  title,
                                  content,
                                  primary,
                                  dto.getVisibility(),
                                  dto.getPartitionId(),
                                  dto.getTags(),
                                  dto.getComUseTags())
                                  .exception(new ServiceException(VerifyConstant.NOT_ALL_BLANK)))
                  .doVerify();

        Article article = jsonNullableObjectMapper.convertValue(dto, Article.class);
        if (mapper.update(article, true) <= 0) {
            return null;
        }
        return getById(dto.getId());
    }

    @Override
    public Boolean deleteById(Long id) throws Exception {
        VerifyTool.ofLogin(ArticleVerifyNode.id(id, this))
                  .doVerify();

        return mapper.deleteById(id) == 1;
    }
}

package com.wolfhouse.wolfhouseblog.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.EsExceptionConstant;
import com.wolfhouse.wolfhouseblog.common.constant.es.ElasticConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.EsUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.config.ElasticSearchConfig;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDraftDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;

/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleElasticServiceImpl implements ArticleService {
    private final ElasticsearchClient client;
    private final ElasticSearchConfig config;
    private final ServiceAuthMediator mediator;
    @Resource(name = "jsonNullableObjectMapper")
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        log.info("正在初始化 ES 索引库...");

        try {
            boolean flag = client.indices()
                                 .exists(req -> req.index(ElasticConstant.ARTICLE_INDEX))
                                 .value();
            if (flag) {
                // 索引库已存在
                log.info("索引库 {} 已存在", ElasticConstant.ARTICLE_INDEX);
                return;
            }

            // 初始化索引
            client.indices()
                  .create(r -> {
                      r.index(ElasticConstant.ARTICLE_INDEX);
                      r.mappings(f -> {
                          f.properties(ElasticConstant.ARTICLE_MAPPING);
                          return f;
                      });
                      r.settings(s -> {
                          s.maxResultWindow(config.maxResultWindow);
                          return s;
                      });
                      return r;
                  });
            log.info("索引库 {} 初始化完成", ElasticConstant.ARTICLE_INDEX);
        } catch (IOException e) {
            log.error("初始化 ElasticSearch 索引失败");
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void saveBatch(List<Article> articles) throws IOException {
        int size = articles.size();
        final int batch = 200;
        int index = 0;
        int round = size / batch;

        log.info("正在执行批量插入: {}", size);

        for (; index < round; index++) {
            var builder = new BulkRequest.Builder();
            // 本轮计数，是实际的个数索引，会更新
            int roundCurrent = index * batch;
            log.info("----- 第 {} 轮 -----", index + 1);
            do {
                final int nowIndex = roundCurrent;
                builder.operations(op -> {
                    // 构建
                    op.index(idx -> {
                        idx.index(ElasticConstant.ARTICLE_INDEX);
                        idx.id(articles.get(nowIndex)
                                       .getId()
                                       .toString());
                        idx.document(articles.get(nowIndex));
                        return idx;
                    });
                    return op;
                });
                roundCurrent++;
            } while (roundCurrent % batch != 0 && roundCurrent < size);

            client.bulk(builder.build());
        }
    }

    public void saveOne(Article article) {
        try {
            client.create(c -> {
                c.index(ElasticConstant.ARTICLE_INDEX);
                c.id(article.getId()
                            .toString());
                c.document(article);
                return c;
            });
        } catch (IOException e) {
            log.error("存储文章至 ES 失败！Article: {}", article, e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        long pageSize = dto.getPageSize();
        SearchRequest.Builder builder = new SearchRequest.Builder();
        // 索引库
        builder.index(ElasticConstant.ARTICLE_INDEX);

        // 指定查询列
        if (columns.length != 0) {
            builder.source(v -> {
                v.filter(f -> f.includes(Arrays.stream(columns)
                                               .map(QueryColumn::getName)
                                               .toList()));
                return v;
            });
        }
        // 分页参数
        buildPagination(builder, (int) (dto.getPageSize() * (dto.getPageNumber() - 1)), (int) pageSize);

        // TODO 复杂查询未实现

        SearchResponse<Article> response = client.search(builder.build(), Article.class);
        return toPage(response, dto.getPageNumber(), pageSize);
    }

    @Override
    public PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) throws Exception {
        long pageSize = dto.getPageSize();
        SearchResponse<ArticleBriefVo> response = client.search(r -> {
            r.index(ElasticConstant.ARTICLE_INDEX);
            // 分页参数
            buildPagination(r, (int) (dto.getPageSize() * (dto.getPageNumber() - 1)), (int) pageSize);
            // 获取总条数
            return r;
        }, ArticleBriefVo.class);
        return PageResult.of(toPage(response, dto.getPageNumber(), pageSize));
    }

    @Override
    public List<ArticleBriefVo> getBriefByIds(Collection<Long> articleIds) throws IOException {
        Long login = mediator.loginUserOrNull();
        var reqBuilder = new SearchRequest.Builder();

        reqBuilder.index(ElasticConstant.ARTICLE_INDEX);
        // 指定查询 ID
        reqBuilder.query(v -> {
            // 查询 ID 符合的，公开 或 私密且作者为登录用户的
            v.terms(t -> {
                // 指定查询字段
                t.field(ARTICLE.ID.getName());
                // 指定 ID 范围
                t.terms(vt -> vt.value(articleIds.stream()
                                                 .map(FieldValue::of)
                                                 .toList()));
                return t;
            });
            // 可见性条件
            v.bool(b -> EsUtil.reachableBuilder(b, login, ARTICLE.VISIBILITY.getName(), ARTICLE.AUTHOR_ID.getName()));
            return v;
        });
        return EsUtil.searchHitsToList(client.search(reqBuilder.build(), ArticleBriefVo.class));
    }

    @Override
    public PageResult<ArticleVo> getQueryVo(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception {
        return PageResult.of(queryBy(dto), ArticleVo.class);
    }

    @Override
    public ArticleVo getVoById(Long id) throws Exception {
        ArticleQueryPageDto dto = new ArticleQueryPageDto();
        dto.setId(JsonNullable.of(id));
        return BeanUtil.copyProperties(queryBy(dto), ArticleVo.class);
    }

    @Override
    public Article post(ArticleDto dto) {
        throw new ServiceException(EsExceptionConstant.TO_METHOD.formatted("saveOne"));
    }

    @Override
    public ArticleVo draft(ArticleDraftDto dto) {
        throw new ServiceException(EsExceptionConstant.METHOD_NOT_SUPPORT);
    }

    @Override
    public ArticleVo getDraft() {
        throw new ServiceException(EsExceptionConstant.METHOD_NOT_SUPPORT);
    }

    @Override
    public Boolean unDraft() {
        throw new ServiceException(EsExceptionConstant.METHOD_NOT_SUPPORT);
    }

    @Override
    public ArticleVo update(ArticleUpdateDto dto) throws Exception {
        var builder = new UpdateRequest.Builder<Article, Map<?, ?>>();
        builder.index(ElasticConstant.ARTICLE_INDEX);
        builder.id(dto.getId()
                      .toString());

        builder.doc(objectMapper.convertValue(dto, Map.class));
        UpdateResponse<Article> resp = client.update(builder.build(), Article.class);
        if (resp.get() == null) {
            return null;
        }
        return BeanUtil.copyProperties(resp.get()
                                           .source(), ArticleVo.class);
    }

    @Override
    public Boolean deleteById(Long id) throws Exception {
        var reqBuilder = new DeleteRequest.Builder();
        reqBuilder.index(ElasticConstant.ARTICLE_INDEX);
        reqBuilder.id(id.toString());
        DeleteResponse resp = client.delete(reqBuilder.build());
        return "deleted".equals(resp.result()
                                    .jsonValue());
    }

    @Override
    public Boolean isArticleReachable(Long userId, Long articleId) throws Exception {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(ElasticConstant.ARTICLE_INDEX);
        builder.query(
            q -> {
                q.bool(b -> EsUtil.reachableBuilder(b,
                                                    userId,
                                                    ARTICLE.VISIBILITY.getName(),
                                                    ARTICLE.AUTHOR_ID.getName()));
                q.term(t -> t.field(ARTICLE.ID.getName())
                             .value(articleId));
                return q;
            });
        return !EsUtil.searchHitsToList(client.search(builder.build()))
                      .isEmpty();
    }

    @Override
    public Boolean isArticleOwner(Long articleId, Long login) {
        var reqBuilder = new SearchRequest.Builder();
        reqBuilder.index(ElasticConstant.ARTICLE_INDEX)
                  .query(q -> q.bool(
                      b -> b.must(
                          bq -> {
                              // 指定文章 ID
                              bq.term(t -> t.field(ARTICLE.ID.getName())
                                            .value(articleId));
                              // 作者 ID
                              bq.term(t -> t.field(ARTICLE.AUTHOR_ID.getName())
                                            .value(login));
                              return bq;
                          })));

        try {
            return !EsUtil.searchHitsToList(client.search(reqBuilder.build()))
                          .isEmpty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseMapper<Article> getMapper() {
        throw new ServiceException(EsExceptionConstant.METHOD_NOT_SUPPORT);
    }

    private void buildPagination(SearchRequest.Builder builder, int from, int size) {
        builder.trackTotalHits(b -> {
            b.enabled(true);
            return b;
        });
        builder.from(from);
        builder.size(size);
    }

    private <T> Page<T> toPage(SearchResponse<T> response, long pageNum, long pageSize) {
        Page<T> page = new Page<>();
        // 记录
        page.setRecords(EsUtil.searchHitsToList(response));

        // 总行数
        TotalHits total = response.hits()
                                  .total();

        page.setTotalRow(total == null ? 0 : total.value());
        page.setPageNumber(pageNum);
        page.setPageSize(pageSize);
        page.setTotalPage(page.getTotalRow() / page.getPageSize());
        return page;
    }
}

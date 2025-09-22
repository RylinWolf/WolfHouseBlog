package com.wolfhouse.wolfhouseblog.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.mybatisflex.core.paginate.Page;
import com.wolfhouse.wolfhouseblog.common.constant.es.ElasticConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.config.ElasticSearchConfig;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author linexsong
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleElasticService {
    private final ElasticsearchClient client;
    private final ElasticSearchConfig config;

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

    public PageResult<ArticleBriefVo> getBriefVoList(ArticleQueryPageDto dto) throws IOException {
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
        var hits = response.hits()
                           .hits();
        // 记录
        page.setRecords(hits.stream()
                            .map(Hit::source)
                            .toList());

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

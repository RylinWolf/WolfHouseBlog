package com.wolfhouse.wolfhouseblog.common.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.mybatisflex.core.paginate.Page;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef.ARTICLE;

/**
 * @author linexsong
 */
public class EsUtil {

    /**
     * 构建一个用于判断文章可访问性的查询构造器。
     * 查询条件包括：
     * 1. 公开文章（基于 `vField`）。
     * 2. 私密文章（基于 `lField`），且作者为登录用户。
     *
     * @param b      BoolQuery.Builder，Elasticsearch 查询的构建器对象。
     * @param login  登录用户的唯一标识（如用户 ID）。如果为 null，则忽略私密文章的条件。
     * @param vField 表示访问权限字段的名称。
     * @param lField 表示标识用户字段的名称。
     * @return 更新后的 BoolQuery.Builder，包含了新增的条件。
     */
    public static BoolQuery.Builder reachableBuilder(BoolQuery.Builder b,
                                                     Long login,
                                                     String vField,
                                                     String lField) {
        b.should(s -> {
            // 公开
            s.term(sv -> sv.field(vField)
                           .value(VisibilityEnum.PUBLIC.value));
            // 私密且作者为登录用户
            if (login != null) {
                s.term(sv -> sv.field(lField)
                               .value(login));
            }
            return s;
        });
        return b;
    }

    public static <T> List<T> searchHitsToList(SearchResponse<T> resp) {
        return resp.hits()
                   .hits()
                   .stream()
                   .map(Hit::source)
                   .toList();
    }

    /**
     * 将 Elasticsearch 查询响应对象中的搜索结果转化为具有高亮处理的结果列表。
     * 对于每个搜索结果，如果存在高亮字段，将高亮内容合并并设置回对应的字段。
     *
     * @param resp 查询响应对象，包含搜索结果和高亮内容。
     * @return 一个列表，其中包含处理高亮后的搜索结果对象。
     * 如果某些结果没有高亮内容，则返回其原始对象。
     */
    public static <T> List<T> hitsWithHighLightToList(SearchResponse<T> resp) {
        return resp.hits()
                   .hits()
                   .stream()
                   .map(h -> {
                       T source = h.source();
                       if (source == null) {
                           return null;
                       }
                       // 获取高亮结果
                       Map<String, List<String>> highlight = h.highlight();
                       highlight.forEach((k, v) -> {
                           if (v == null || v.isEmpty()) {
                               return;
                           }
                           try {
                               // 获取字段
                               Field field = source.getClass()
                                                   .getDeclaredField(k);
                               field.setAccessible(true);
                               // 整合高亮结果，设置给对应字段
                               field.set(source, String.join("", v));
                           } catch (NoSuchFieldException | IllegalAccessException e) {
                               throw new RuntimeException(e);
                           }
                       });
                       return source;
                   })
                   .toList();
    }

    public static Query matchQuery(String name, String t) {
        return new MatchQuery.Builder().field(name)
                                       .query(t)
                                       .build()
                                       ._toQuery();
    }

    public static Query termLongQuery(String name, Long id) {
        return new TermQuery.Builder().field(name)
                                      .value(id)
                                      .build()
                                      ._toQuery();
    }

    public static Query dateRangeQuery(Object startDate, Object endDate) {
        DateRangeQuery.Builder dateBuilder = new DateRangeQuery.Builder();
        if (!BeanUtil.isAnyNotBlank(startDate, endDate)) {
            return null;
        }
        dateBuilder.field(ARTICLE.POST_TIME.getName());
        if (startDate != null) {
            dateBuilder.gte(startDate.toString());
        }
        if (endDate != null) {
            dateBuilder.lte(endDate.toString());
        }
        return dateBuilder.build()
                          ._toRangeQuery()
                          ._toQuery();
    }

    public static Highlight highLight(Collection<String> highlightFields) {
        // 构建每个字段的高亮配置（仅使用通用且兼容的字段级参数）
        var highLightField = HighlightField.of(f -> f);
        // 在顶层设置统一的 pre_tags / post_tags（ES 要求）
        return Highlight.of(v -> v
            .preTags("<em class='highlight'>")
            .postTags("</em>")
            .requireFieldMatch(false)
            .noMatchSize(0)
            .fragmentSize(50)
            .fields(highlightFields.stream()
                                   .map(f -> Map.entry(f, highLightField))
                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    public static <T> Page<T> responseToPage(SearchResponse<T> response, long pageNum, long pageSize) {
        return responseToPage(response, pageNum, pageSize, false);
    }

    public static <T> Page<T> responseToPage(SearchResponse<T> response,
                                             long pageNum,
                                             long pageSize,
                                             Boolean highlight) {
        Page<T> page = new Page<>();
        // 记录，若 highlight 字段启用，则自动映射到字段
        page.setRecords(
            highlight != null && highlight ? hitsWithHighLightToList(response) : searchHitsToList(response));

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

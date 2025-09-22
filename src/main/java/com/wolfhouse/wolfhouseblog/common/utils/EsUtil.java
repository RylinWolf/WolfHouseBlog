package com.wolfhouse.wolfhouseblog.common.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;

import java.util.List;

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

    public static Query matchBuilder(String name, String t) {
        return new MatchQuery.Builder().field(name)
                                       .query(t)
                                       .build()
                                       ._toQuery();
    }

    public static Query termBuilder(String name, Long id) {
        return new TermQuery.Builder().field(name)
                                      .value(id)
                                      .build()
                                      ._toQuery();
    }
}

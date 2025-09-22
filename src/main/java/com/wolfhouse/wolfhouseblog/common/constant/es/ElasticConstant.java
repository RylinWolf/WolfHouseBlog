package com.wolfhouse.wolfhouseblog.common.constant.es;

import co.elastic.clients.elasticsearch._types.mapping.DateProperty;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;

import java.util.Map;

/**
 * @author linexsong
 */
public class ElasticConstant {
    public static final String ARTICLE_INDEX = "article";
    public static final Map<String, Property> ARTICLE_MAPPING = Map.ofEntries(
        Map.entry("id", ofProperty("keyword", true, null)),
        Map.entry("title", ofProperty("text", true, "ik_max_word")),
        Map.entry("primary", ofProperty("text", false, null)),
        Map.entry("author_id", ofProperty("keyword", true, null)),
        Map.entry("content", ofProperty("text", true, "ik_max_word")),
        Map.entry("post_time", ofProperty("date", true, null)),
        Map.entry("edit_time", ofProperty("date", false, null)),
        Map.entry("visibility", ofProperty("keyword", true, null)),
        Map.entry("partition_id", ofProperty("keyword", false, null)),
        Map.entry("tags", ofProperty("text", true, "ik_smart")),
        Map.entry("com_use_tags", ofProperty("keyword", false, null)));


    private static Property ofProperty(String type, boolean index, String analyzer) {
        return switch (type) {
            case "text" -> TextProperty.of(v -> {
                                           v.index(index);
                                           if (analyzer != null && !analyzer.isBlank()) {
                                               v.analyzer(analyzer);
                                           }
                                           return v;
                                       })
                                       ._toProperty();
            case "keyword" -> KeywordProperty.of(v -> {
                                                 v.index(index);
                                                 return v;
                                             })
                                             ._toProperty();
            case "date" -> DateProperty.of(v -> {
                                           v.index(index);
                                           v.format("yyyy-MM-dd HH:mm:ss");
                                           return v;
                                       })
                                       ._toProperty();
            case null, default -> Property.of(v -> v);
        };
    }
}

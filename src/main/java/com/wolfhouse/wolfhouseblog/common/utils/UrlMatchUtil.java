package com.wolfhouse.wolfhouseblog.common.utils;

import com.wolfhouse.wolfhouseblog.common.constant.SecurityConstant;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;

/**
 * @author linexsong
 */
public class UrlMatchUtil {
    @SuppressWarnings("FieldMayBeFinal")
    private List<PathPattern> publicUrlList;
    private List<PathPattern> whiteList;

    private UrlMatchUtil() {
        this.publicUrlList = Arrays.stream(SecurityConstant.PUBLIC_URLS)
                                   .map(PathPatternParser.defaultInstance::parse)
                                   .toList();
        this.whiteList = Arrays.stream(SecurityConstant.STATIC_PATH_WHITELIST)
                               .map(PathPatternParser.defaultInstance::parse)
                               .toList();
    }

    private static class SingletonHolder {
        private static UrlMatchUtil INSTANCE;

        private static UrlMatchUtil getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new UrlMatchUtil();
            }
            return INSTANCE;
        }
    }

    public static UrlMatchUtil instance() {
        return SingletonHolder.getInstance();
    }

    public List<PathPattern> add(String... urls) {
        this.publicUrlList.addAll(
            Arrays.stream(urls)
                  .map(PathPatternParser.defaultInstance::parse)
                  .toList());
        return this.publicUrlList;
    }

    public List<PathPattern> remove(String... urls) {
        this.publicUrlList.removeAll(
            Arrays.stream(urls)
                  .map(PathPatternParser.defaultInstance::parse)
                  .toList());
        return this.publicUrlList;
    }

    public Boolean isPublic(String url) {
        return this.publicUrlList.stream()
                                 .anyMatch(p -> p.matches(PathContainer.parsePath(url)))
               || this.whiteList.stream()
                                .anyMatch(p -> p.matches(PathContainer.parsePath(url)));
    }
}

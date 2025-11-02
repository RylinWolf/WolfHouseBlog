package com.wolfhouse.wolfhouseblog.mail;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * FreeMarker 配置
 *
 * @author linexsong
 */
@Configuration
@RequiredArgsConstructor
public class FreeMarkerConfig {
    private final freemarker.template.Configuration configuration;

    @PostConstruct
    public freemarker.template.Configuration freeMarkerConfiguration() {
        var cfg = configuration;
        // 从 classpath:templates 加载模板
        cfg.setClassLoaderForTemplateLoading(
            Thread.currentThread()
                  .getContextClassLoader(),
            "templates");
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }
}

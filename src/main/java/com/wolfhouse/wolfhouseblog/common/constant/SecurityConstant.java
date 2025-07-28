package com.wolfhouse.wolfhouseblog.common.constant;

/**
 * @author linexsong
 */
public class SecurityConstant {
    public static final String[] STATIC_PATH_WHITELIST = {
            "/",
            "/js/**",
            "/css/**",
            "/img/**",
            "/fonts/**",
            "/index.html",
            "/favicon.ico",
            "/doc.html",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/**"
    };

    public static final String[] STATIC_LOCATION_WHITELIST = {
            "classpath:/static/",
            "classpath:/public/",
            "classpath:/META-INF/resources/"
    };

    public static final String[] PUBLIC_URLS = {
            "/user/login",
            "/user/register"
    };
}

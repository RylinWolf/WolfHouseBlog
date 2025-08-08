package com.wolfhouse.wolfhouseblog.common.utils;

import com.wolfhouse.wolfhouseblog.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * @author linexsong
 */
@RequiredArgsConstructor
@Component
public class JwtUtil {
    private final JwtProperties properties;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
    }


    /**
     * 根据认证信息生成JWT令牌
     * 使用用户名作为主题，并设置过期时间和签名
     *
     * @param authentication Spring Security的认证信息对象，包含用户名等信息
     * @return 生成的JWT令牌字符串
     */
    public String getToken(Authentication authentication) {
        String id = authentication.getName();

        return getToken(id);
    }

    public String getToken(String id) {
        return Jwts.builder()
                   .setSubject(id)
                   .setExpiration(new Date(System.currentTimeMillis() + properties.expiration()))
                   .signWith(key(), SignatureAlgorithm.HS256)
                   .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }
}

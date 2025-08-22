package com.wolfhouse.wolfhouseblog.auth.permission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author linexsong
 */
@Slf4j
@Component(value = "ss")
public class PermissionService {
    public Boolean hasRole(String... role) {
        Set<String> roles = Arrays.stream(role)
                                  .map(r -> "ROLE_" + r)
                                  .collect(Collectors.toSet());
        return getAuth().getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
                        .containsAll(roles);
    }

    public Boolean hasAnyRole(String... role) {
        Set<String> roles = Arrays.stream(role)
                                  .map(r -> "ROLE_" + r)
                                  .collect(Collectors.toSet());
        return hasAny(getAuth().getAuthorities(), roles);
    }

    public Boolean hasPerm(String... permission) {
        return getAuth().getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
                        .containsAll(Set.of(permission));

    }

    private Authentication getAuth() {
        return SecurityContextHolder.getContext()
                                    .getAuthentication();
    }

    public Boolean hasAnyPerm(String... permission) {
        log.info("当前权限{}: 需要任意权限【{}】", getAuth().getAuthorities(), permission);
        return hasAny(
             getAuth().getAuthorities(), Set.of(permission));
    }

    /**
     * 判断已有权限集合中，是否包含任意权限
     *
     * @param roles      已有权限集合
     * @param permission 指定权限
     * @return 是否包含任意权限
     */
    private Boolean hasAny(Collection<? extends GrantedAuthority> roles, Set<String> permission) {
        Set<String> hasRoles = roles.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toSet());
        // 若形参长度 加 当前权限长度 等于合并后长度，则不拥有任何权限
        Integer r = permission.size();
        Integer h = roles.size();
        hasRoles.addAll(permission);
        return hasRoles.size() != r + h;

    }
}

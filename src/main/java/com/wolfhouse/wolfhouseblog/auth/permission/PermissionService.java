package com.wolfhouse.wolfhouseblog.auth.permission;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author linexsong
 */
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
        Set<String> hasRoles = getAuth().getAuthorities()
                                        .stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toSet());
        // 若形参长度 加 当前权限长度 等于合并后长度，则不拥有任何权限
        Integer r = roles.size();
        Integer h = hasRoles.size();
        hasRoles.addAll(roles);
        return hasRoles.size() != r + h;
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
}

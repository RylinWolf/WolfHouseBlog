package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author linexsong
 */
@Data
@Table("authority")
public class Authority implements GrantedAuthority {
    private Long id;
    private String permissionCode;
    private String permissionName;

    @Override
    public String getAuthority() {
        return permissionCode;
    }
}

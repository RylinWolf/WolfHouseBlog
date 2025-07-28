package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginVo {
    private String token;

    public static UserLoginVo token(String token) {
        return new UserLoginVo(token);
    }
}

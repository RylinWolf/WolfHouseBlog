package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author linexsong
 */
@Component
@Data
public class UserVo {
    private Long id;
    private String username;
    private String nickname;
    private String account;
    private String avatar;
    private String personalStatus;
    private String phone;
    private String email;
    private LocalDate birth;
    private LocalDate registerDate;
}

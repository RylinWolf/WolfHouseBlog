package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * @author linexsong
 */
@Data
@Table("users")
@Component
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Id
    protected Long id;
    @Size(min = 2, max = 20)
    protected String username;
    protected String account;
    protected String avatar;

    @Size(max = 50)
    protected String personalStatus;

    @Size(max = 20)
    protected String phone;

    @Email
    protected String email;
    protected Date birth;
    protected Date registerDate;
}

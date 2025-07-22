package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("user_auth")
public class UserAuth {
    @Id
    private Long userId;
    @Size(min = 6, max = 20)
    private String password;
    private Boolean isEnabled;
    @Column(isLogicDelete = true)
    private Boolean isDeleted;
}

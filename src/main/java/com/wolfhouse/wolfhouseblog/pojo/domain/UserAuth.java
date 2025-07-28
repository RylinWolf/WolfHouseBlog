package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@Schema(name = "用户验证实体")
@Table("user_auth")
public class UserAuth {
    @Id
    @Column(value = "id")
    private Long userId;
    @Size(min = 6, max = 20)
    private String password;
    private Boolean isEnabled;
    @Column(isLogicDelete = true)
    private Boolean isDeleted;
}

package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

/**
 * @author linexsong
 */
@Data
@Component
@EqualsAndHashCode(callSuper = false)
public class UserSubDto extends PageDto {
    private Long fromUser;
    @NotNull
    private Long toUser;
}

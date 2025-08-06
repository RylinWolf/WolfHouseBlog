package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
@Table("subscribe")
public class Subscribe {
    private Long id;
    private Long fromUser;
    private Long toUser;
}

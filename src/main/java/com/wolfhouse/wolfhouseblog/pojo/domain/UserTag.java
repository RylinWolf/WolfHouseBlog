package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @author linexsong
 */
@Table("user_tag")
@Data
public class UserTag {
    private Long userId;
    private Long tagId;
}

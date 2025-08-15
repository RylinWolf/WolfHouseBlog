package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Table("user_tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTag {
    private Long userId;
    private Long tagId;
}

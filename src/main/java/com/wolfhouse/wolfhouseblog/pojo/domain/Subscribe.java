package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @author linexsong
 */
@Data
@Table("subscribe")
public class Subscribe {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long fromUser;
    private Long toUser;
}

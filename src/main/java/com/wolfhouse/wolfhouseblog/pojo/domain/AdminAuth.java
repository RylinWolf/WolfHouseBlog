package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Table("admin_authority")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuth {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long adminId;
    @Column("authority_id")
    private Long authId;
}

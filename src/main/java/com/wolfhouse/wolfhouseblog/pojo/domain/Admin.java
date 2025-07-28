package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author linexsong
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("admins")
public class Admin extends User {
    private String name;
    @Id
    private Long id;
    private Long userId;

    @RelationManyToMany(
            joinTable = "admin_authority",
            selfField = "id", joinSelfColumn = "admin_id",
            targetField = "id", joinTargetColumn = "authority_id")
    private List<Authority> authorities;

}

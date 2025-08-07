package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.mybatisflex.core.query.QueryColumn;
import lombok.Data;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.UserTableDef.USER;

/**
 * @author linexsong
 */
@Data
public class UserBriefVo {
    private Long id;
    private String account;
    private String nickname;
    private String avatar;
    private String personalStatus;

    public static final QueryColumn[] COLUMNS = {
            USER.ID,
            USER.ACCOUNT,
            USER.NICKNAME,
            USER.AVATAR,
            USER.PERSONAL_STATUS};
}

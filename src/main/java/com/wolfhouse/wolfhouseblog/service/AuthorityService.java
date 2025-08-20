package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.AuthorityByIdDto;

/**
 * @author linexsong
 */
public interface AuthorityService extends IService<Authority> {

    Boolean addAuthorityByIds(AuthorityByIdDto dto);
}

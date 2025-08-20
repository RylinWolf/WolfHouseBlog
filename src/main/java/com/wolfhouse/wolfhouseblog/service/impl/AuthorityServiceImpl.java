package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.services.AuthorityConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.mapper.AuthorityMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.domain.table.AuthorityTableDef;
import com.wolfhouse.wolfhouseblog.pojo.dto.AuthorityByIdDto;
import com.wolfhouse.wolfhouseblog.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements AuthorityService {

    @Override
    public Boolean addAuthorityByIds(AuthorityByIdDto dto) {
        Set<Long> ids = dto.getIds();
        if (mapper.selectCountByQuery(QueryWrapper.create()
                                                  .where(AuthorityTableDef.AUTHORITY.ID.in(ids))) != ids.size()) {
            throw new ServiceException(AuthorityConstant.NOT_EXIST);
        }
        return true;
    }
}

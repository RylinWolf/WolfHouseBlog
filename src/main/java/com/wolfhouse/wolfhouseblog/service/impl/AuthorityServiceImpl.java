package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.mapper.AuthorityMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.service.AuthorityService;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements AuthorityService {

}

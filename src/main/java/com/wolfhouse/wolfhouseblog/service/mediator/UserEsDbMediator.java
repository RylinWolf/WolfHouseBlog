package com.wolfhouse.wolfhouseblog.service.mediator;

import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;

/**
 * @author linexsong
 */
public interface UserEsDbMediator {
    UserVo getUserVoById(Long id) throws Exception;
}

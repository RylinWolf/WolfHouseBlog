package com.wolfhouse.wolfhouseblog.mq;

import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.pojo.domain.Authority;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqUserAuthDto;
import com.wolfhouse.wolfhouseblog.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class MqTools {
    private final AdminService adminService;

    public void setLoginAuth(MqUserAuthDto dto) throws Exception {
        Long loginId = dto.getUserId();
        ServiceUtil.setLoginUser(loginId);
        List<Authority> authorities = adminService.getAuthorities(loginId);
        ServiceUtil.setAuthorities(authorities);
    }
}

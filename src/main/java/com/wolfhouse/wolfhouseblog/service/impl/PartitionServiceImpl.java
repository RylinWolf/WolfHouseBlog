package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition.PartitionVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.PartitionMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class PartitionServiceImpl extends ServiceImpl<PartitionMapper, Partition> implements PartitionService {
    @Override
    public PartitionVo getPartitionVos() {
        // TODO
        return null;
    }

    @Override
    public PartitionVo getPartitionVoByName(String name) {
        // TODO
        return null;
    }

    public PartitionVo addPartition(PartitionDto dto) {
        // 验证字段
        return null;
    }
}

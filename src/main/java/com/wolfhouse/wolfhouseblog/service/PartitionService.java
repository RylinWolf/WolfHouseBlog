package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;

/**
 * @author linexsong
 */
public interface PartitionService extends IService<Partition> {

    PartitionVo addPartition(PartitionDto dto);

    PartitionVo getPartitionVos();

    PartitionVo getPartitionVoByName(String name);

    Boolean isUserPartitionExist(Long userId, Long partitionId);
}

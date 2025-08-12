package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;

import java.util.List;

/**
 * @author linexsong
 */
public interface PartitionService extends IService<Partition> {

    /**
     * 添加新的分区
     *
     * @param dto 分区数据传输对象
     * @return 更新后的分区视图对象列表
     */
    List<PartitionVo> addPartition(PartitionDto dto);

    /**
     * 获取当前登录用户的所有分区视图对象
     *
     * @return 分区视图对象列表
     * @throws Exception 未登录或验证失败时抛出异常
     */
    List<PartitionVo> getPartitionVos() throws Exception;

    /**
     * 根据分区名称获取分区视图对象
     *
     * @param name 分区名称
     * @return 分区视图对象
     */
    PartitionVo getPartitionVoByName(String name) throws Exception;

    /**
     * 检查指定用户是否拥有指定分区
     *
     * @param userId      用户ID
     * @param partitionId 分区ID
     * @return true表示存在，false表示不存在
     */
    Boolean isUserPartitionExist(Long userId, Long partitionId);

    /**
     * 获取指定用户特定分区的子结构
     *
     * @param userId      用户ID
     * @param partitionId 分区ID
     * @return 分区视图对象列表，包含层级结构
     */
    List<PartitionVo> getPartitionVoStructure(Long userId, Long partitionId);

    /**
     * 获取指定用户的所有分区结构
     *
     * @param userId 用户ID
     * @return 分区视图对象列表，包含层级结构
     */
    List<PartitionVo> getPartitionVoStructure(Long userId);

}

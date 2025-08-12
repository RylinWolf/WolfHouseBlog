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
     * 添加新的分区。此方法会验证当前登录用户的权限，并将新分区添加到用户的分区列表中。
     * 如果指定了父分区ID，新分区将作为其子分区创建。
     *
     * @param dto 分区数据传输对象，包含分区名称、描述、父分区ID等信息
     * @return 更新后的分区视图对象列表，包含新添加的分区及其相关分区
     * @throws Exception 业务异常
     */
    List<PartitionVo> addPartition(PartitionDto dto) throws Exception;

    /**
     * 获取当前登录用户的所有分区视图对象
     *
     * @return 分区视图对象列表
     * @throws Exception 未登录或验证失败时抛出异常
     */
    List<PartitionVo> getPartitionVos() throws Exception;

    /**
     * 根据分区名称获取分区视图对象。此方法会查找并返回指定名称的分区的详细信息。
     *
     * @param name 分区名称，用于查找对应的分区
     * @return 分区视图对象，包含分区的详细信息。如果未找到对应分区则返回null
     * @throws Exception 当查询过程中发生错误，或者用户没有权限访问该分区时抛出异常
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

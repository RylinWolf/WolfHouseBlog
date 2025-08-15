package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;

import java.util.SortedSet;

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
    SortedSet<PartitionVo> addPartition(PartitionDto dto) throws Exception;

    /**
     * 获取当前登录用户的所有分区视图对象
     *
     * @return 分区视图对象列表
     * @throws Exception 未登录或验证失败时抛出异常
     */
    SortedSet<PartitionVo> getPartitionVos() throws Exception;

    /**
     * 获取当前登录用户的指定分区及其子分区的视图对象。此方法会返回指定分区ID的分区
     * 及其所有子分区的视图对象列表。
     *
     * @param partitionId 要获取的分区ID
     * @return 分区视图对象列表，包含指定分区及其子分区
     * @throws Exception 未登录、验证失败或分区不存在时抛出异常
     */
    SortedSet<PartitionVo> getPartitionVos(Long partitionId) throws Exception;


    /**
     * 根据分区名称获取分区视图对象。此方法会查找并返回指定名称的分区的详细信息。
     *
     * @param name 分区名称，用于查找对应的分区
     * @return 分区视图对象，包含分区的详细信息。如果未找到对应分区则返回null
     * @throws Exception 当查询过程中发生错误，或者用户没有权限访问该分区时抛出异常
     */
    PartitionVo getPartitionVoByName(String name) throws Exception;

    /**
     * 检查指定用户是否拥有指定分区。此方法将验证给定用户ID是否拥有指定的分区权限。
     *
     * @param userId      用户ID，要检查权限的用户标识
     * @param partitionId 分区ID，要检查的分区标识
     * @return true表示用户拥有该分区，false表示用户不拥有该分区
     * @throws Exception 当验证过程中发生错误（如数据库访问失败）、
     *                   用户ID无效或分区ID不存在时抛出异常
     */
    Boolean isUserPartitionExist(Long userId, Long partitionId) throws Exception;

    /**
     * 检查当前登录用户是否拥有指定分区。此方法是{@link #isUserPartitionExist(Long, Long)}的简化版本，
     * 使用当前登录用户的ID进行验证。
     *
     * @param partitionId 待检查的分区ID
     * @return true表示当前用户拥有该分区，false表示不拥有
     * @throws Exception 当验证过程中发生错误，或者用户未登录时抛出异常
     */
    Boolean isUserPartitionExist(Long partitionId) throws Exception;

    /**
     * 更新分区信息。此方法将根据提供的更新数据传输对象修改指定分区的信息。
     * 只会更新dto中非空的字段。
     *
     * @param dto 分区更新数据传输对象，包含需要更新的分区信息
     * @return 更新后的分区视图对象列表
     * @throws Exception 当更新过程中发生错误，或者用户没有权限更新该分区时抛出异常
     */
    SortedSet<PartitionVo> updatePatch(PartitionUpdateDto dto) throws Exception;

    /**
     * 删除单个分区。此方法将删除指定ID的分区，如果该分区存在子分区，则子分区不会被删除。
     *
     * @param partitionId 要删除的分区ID
     * @return 删除后的分区视图对象列表
     * @throws Exception 当删除过程中发生错误，或者用户没有权限删除该分区时抛出异常
     */
    SortedSet<PartitionVo> deleteOne(Long partitionId) throws Exception;

    /**
     * 批量删除分区。此方法将删除指定ID的分区及其所有子分区。
     *
     * @param partitionId 要删除的分区ID
     * @return 删除后的分区视图对象列表
     * @throws Exception 当删除过程中发生错误，或者用户没有权限删除该分区时抛出异常
     */
    SortedSet<PartitionVo> deleteBatch(Long partitionId) throws Exception;
}

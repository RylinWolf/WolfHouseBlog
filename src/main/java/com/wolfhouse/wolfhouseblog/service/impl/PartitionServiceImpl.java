package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition.PartitionVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JsonNullableUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.PartitionMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.PartitionTableDef.PARTITION;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class PartitionServiceImpl extends ServiceImpl<PartitionMapper, Partition> implements PartitionService {
    private final UserAuthService authService;

    @Override
    public SortedSet<PartitionVo> getPartitionVos() throws Exception {
        return getPartitionVos(null);
    }

    @Override
    public SortedSet<PartitionVo> getPartitionVoStructure(Long userId) {
    public SortedSet<PartitionVo> getPartitionVos(Long partitionId) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        VerifyTool.of(UserVerifyNode.id(authService)
                                    .target(login))
                  .doVerify();
        return getPartitionVoStructure(login, partitionId);
    }

    /**
     * 根据用户ID获取分区结构数据。
     *
     * @param userId 用户的唯一标识ID
     * @return 排序后的分区视图对象集合
     */
    private SortedSet<PartitionVo> getPartitionVoStructure(Long userId) {
        return getPartitionVoStructure(userId, null);
    }

    /**
     * 获取分区视图对象的结构。
     *
     * @param userId      用户ID，用于筛选分区信息。
     * @param partitionId 分区ID，如果为null，将获取用户的所有分区；否则获取指定分区及其子分区。
     * @return 排序后的分区视图对象集合。
     */
    private SortedSet<PartitionVo> getPartitionVoStructure(Long userId, Long partitionId) {
        // TODO 优化逻辑，可以先获取所有父节点，再根据父节点获取其孩子节点

        // 要得到指定 ID 的分区视图，关键在于 获取和指定 ID 有关的全部分区列表
        // 基于分区列表构建结构的部分是通用的

        List<Partition> partitions;
        if (partitionId == null) {
            // 获取用户的全部分区
            partitions = getAllPartitions(userId);
        } else {
            // 获取有关的分区
            partitions = listByIds(getPartitionIdWithChildren(partitionId));
        }

        Map<Long, PartitionVo> partitionMap = partitions.stream()
                                                        .collect(Collectors.toMap(
                                                             Partition::getId,
                                                             p -> BeanUtil.copyProperties(p, PartitionVo.class)));

        // 获取 ID -> 孩子ID 的映射
        PartitionIdMapping mapping = getPartitionIdMapping(partitions);

        // 从森林中去除孤独节点
        mapping.singleIds()
               .forEach(mapping.idMap()::remove);
        List<PartitionVo> singlesList = mapping.singleIds()
                                               .stream()
                                               .map(partitionMap::get)
                                               .toList();

        // 遍历森林，构建 Vo 结构
        // 最大深度遍历
        SortedSet<PartitionVo> vos = deepSearchPartitionVos(
             mapping.idMap(),
             null,
             new HashSet<>(),
             partitionMap);
        vos.addAll(singlesList);
        return vos;
    }

    @NonNull
    private Set<Long> getPartitionIdWithChildren(Long partitionId) {
        // 获取和指定 ID 有关的分区列表，先获取该 ID 的直接孩子，再递归获取孩子的孩子
        Set<Long> ids = new HashSet<>();
        // 获取所有的孩子 ID
        Set<Long> childrenIds = mapper.getChildrenIds(partitionId);
        // 遍历获取子孙 ID
        childrenIds.forEach(id -> {
            ids.addAll(getPartitionIdWithChildren(id));
        });
        // 添加自身
        ids.add(partitionId);

        return ids;
    }


    @NonNull
    private List<Partition> getAllPartitions(Long userId) {
        return mapper.selectListByQuery(
             QueryWrapper.create()
                         .eq(Partition::getUserId, userId)
                         .orderBy(Partition::getOrder, true)
                         .orderBy(Partition::getId, true));
    }

    /**
     * 获取分区到子分区的映射
     *
     * @param partitions 分区列表
     * @return 分区 ID 映射记录类，包括 [idMap - ID 映射, singleIds - 孤独 ID]
     */
    private static PartitionIdMapping getPartitionIdMapping(List<Partition> partitions) {
        // 父分区 - 子分区的 ID 映射，森林结构，层数为 2
        Map<Long, Set<Long>> idMap = new HashMap<>(partitions.size());
        // 即没有孩子，也没有父亲的分区 ID（孤独 ID）
        Set<Long> singleIds = new HashSet<>();

        // 遍历分区
        partitions.forEach(p -> {
            // 每个分区 如果不在 idMap 中，则初始化一个空孩子列表
            // 如果有父节点，则添加到指定父节点的 Key 的孩子列表
            // 如果无父节点，则该节点为根节点
            Long pid = p.getId();
            Long parentId = p.getParentId();

            if (!idMap.containsKey(pid)) {
                // 初始化孩子分区列表
                idMap.put(pid, new HashSet<>());
                // 初始化孤独 ID，仅在当前节点还未处理时初始化。
                // 确保 【未处理时 -> 初始化为孤独 ID -> 有父节点则移除】
                // 若移至其他时间添加，则可能导致 【父节点还未处理，未添加为孤独 ID -> 移除孤独 ID（无效）-> 初始化父节点孩子列表并添加
                // -> 后续处理父节点，发现父节点没有双亲，添加至孤独 ID】
                singleIds.add(pid);
            }

            // 有父节点
            if (parentId != null) {
                // 移除孤独 ID
                singleIds.remove(pid);
                singleIds.remove(parentId);

                // 初始化父节点孩子列表
                if (!idMap.containsKey(parentId)) {
                    idMap.put(parentId, new HashSet<>());
                }
                // 添加到父节点的孩子列表
                idMap.get(parentId)
                     .add(pid);
            }
        });
        return new PartitionIdMapping(idMap, singleIds);
    }

    private record PartitionIdMapping(Map<Long, Set<Long>> idMap, Set<Long> singleIds) {}

    /**
     * 最大深度搜索，将指定的 ids 中的每个 ID 获取其 Vo 对象，并填充其后代。
     * 遍历要处理的每个节点，通过分区映射获取到对应的分区 Vo。
     * 若节点有孩子，则会递归获取其所有子孙。
     * 初始的 ids 应为 id 森林的所有键。
     * 每次处理一个 ID 后，将其添加入 processed 列表，表示其要么作为根节点的 Vo ，要么作为孩子已经存在，无需再次处理。
     *
     * @param ids          本次要搜索的 id 列表
     * @param idMap        id 森林，id -> childrenIds
     * @param partitionMap 分区映射, id -> Vo
     * @param processed    已处理过的 id
     * @return 分区 Vo 列表
     */
    private SortedSet<PartitionVo> deepSearchPartitionVos(Map<Long, Set<Long>> idMap,
                                                          Set<Long> ids,
                                                          Set<Long> processed,
                                                          Map<Long, PartitionVo> partitionMap) {
        SortedSet<PartitionVo> vos = new TreeSet<>();
        ids = ids == null ? idMap.keySet() : ids;

        ids.forEach(id -> {
            // 无该 ID
            if (!idMap.containsKey(id)) {
                return;
            }
            // 已处理过，则 vo 已存在
            if (processed.contains(id)) {
                return;
            }
            // 构建当前节点的映射
            PartitionVo vo = partitionMap.get(id);

            // 获取孩子
            Set<Long> children = idMap.get(id);
            // 若有孩子，则递归添加孩子
            if (children != null && !children.isEmpty()) {
                // 获取孩子的搜索结果
                vo.setChildren(deepSearchPartitionVos(
                     idMap,
                     children,
                     processed,
                     partitionMap).toArray(new PartitionVo[0]));
            }

            // 将自身添加至结果，表示已处理过
            vos.add(vo);
            processed.add(id);
        });

        return vos;
    }

    @Override
    public PartitionVo getPartitionVoByName(String name) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        VerifyTool.of(UserVerifyNode.id(authService)
                                    .target(login))
                  .doVerify();

        Partition p = mapper.selectOneByQuery(QueryWrapper.create()
                                                          .eq(Partition::getUserId, login)
                                                          .eq(Partition::getName, name));
        if (p == null) {
            return null;
        }
        return getPartitionVoStructure(login, p.getId()).getFirst();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SortedSet<PartitionVo> addPartition(PartitionDto dto) throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        // 验证字段
        // 暂未使用分区可见性验证，因为自动映射会处理
        VerifyTool.of(
                       UserVerifyNode.id(authService)
                                     .target(login),
                       // 验证分区名格式及是否已存在
                       PartitionVerifyNode.name(this)
                                          .login(login)
                                          .target(dto.getName()),
                       PartitionVerifyNode.id(this)
                                          .target(dto.getParentId())
                                          .allowNull(true))
                  .doVerify();
        Partition partition = BeanUtil.copyProperties(dto, Partition.class);
        partition.setUserId(login);

        if (mapper.insert(partition, true) != 1) {
            throw new ServiceException(PartitionConstant.ADD_FAILED);
        }
        return getPartitionVoStructure(login, partition.getId());
    }

    @Override
    public Boolean isUserPartitionExist(Long userId, Long partitionId) {
        return mapper.selectCountByQuery(QueryWrapper.create()
                                                     .eq(Partition::getUserId, userId)
                                                     .eq(Partition::getId, partitionId)) > 0;
    }
}

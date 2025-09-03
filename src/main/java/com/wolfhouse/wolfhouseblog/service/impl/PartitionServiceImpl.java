package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.JsonNullableUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.commons.NotAllBlankVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.partition.PartitionVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.user.UserIdVerifyNode;
import com.wolfhouse.wolfhouseblog.mapper.PartitionMapper;
import com.wolfhouse.wolfhouseblog.mq.service.MqArticleService;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.mq.MqPartitionChangeDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import jakarta.annotation.PostConstruct;
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
    private final ServiceAuthMediator mediator;
    private final MqArticleService mqArticleService;

    @PostConstruct
    private void init() {
        this.mediator.registerPartition(this);
    }

    @Override
    public SortedSet<PartitionVo> getPartitionVos() throws Exception {
        return getPartitionVos(null);
    }

    @Override
    public SortedSet<PartitionVo> getPartitionVos(Long partitionId) throws Exception {
        Long login = mediator.loginUserOrE();
        return getPartitionVoStructure(login, partitionId);
    }

    /**
     * 根据用户ID获取分区结构数据。
     *
     * @param userId 用户的唯一标识ID
     * @return 排序后的分区视图对象集合
     */
    private SortedSet<PartitionVo> getPartitionVoStructure(Long userId) throws Exception {
        return getPartitionVoStructure(userId, null);
    }

    /**
     * 获取分区视图对象的结构。
     *
     * @param userId      用户ID，用于筛选分区信息。
     * @param partitionId 分区ID，如果为null，将获取用户的所有分区；否则获取指定分区及其子分区。
     * @return 排序后的分区视图对象集合。
     */
    private SortedSet<PartitionVo> getPartitionVoStructure(Long userId, Long partitionId) throws Exception {
        // TODO 优化逻辑，可以先获取所有父节点，再根据父节点获取其孩子节点

        // 要得到指定 ID 的分区视图，关键在于 获取和指定 ID 有关的全部分区列表
        // 基于分区列表构建结构的部分是通用的
        List<Partition> partitions;
        if (partitionId == null) {
            // 获取用户的全部分区
            new UserIdVerifyNode(mediator).target(userId)
                                          .verifyWithCustomE();
            partitions = getAllPartitions(userId);
        } else {
            // 获取有关的分区
            if (!isUserPartitionExist(userId, partitionId)) {
                return null;
            }
            partitions = listByIds(getWithPartitionChildren(partitionId));
        }

        Map<Long, PartitionVo> partitionMap = partitions.stream()
                                                        .collect(Collectors.toMap(
                                                            Partition::getId,
                                                            p -> BeanUtil.copyProperties(p, PartitionVo.class)));

        // 获取 ID -> 孩子 ID 的映射
        // 由于会寻找父节点，所以可能在获取部分节点时会新增 partitionMap 中不包含的节点
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

    /**
     * 递归获取指定分区 ID 对应的分区及其所有子分区的 ID 集合。
     *
     * @param partitionId 指定的分区 ID，表示需要获取其子树的根分区 ID
     * @return 包含指定分区 ID 及其所有子分区 ID 的集合
     */
    @NonNull
    private Set<Long> getWithPartitionChildren(Long partitionId) {
        // 获取和指定 ID 有关的分区列表，先获取该 ID 的直接孩子，再递归获取孩子的孩子
        Set<Long> ids = new HashSet<>();
        // 获取所有的孩子 ID
        Set<Long> childrenIds = mapper.getChildrenIds(partitionId);
        // 遍历获取子孙 ID
        childrenIds.forEach(id -> {
            ids.addAll(getWithPartitionChildren(id));
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
            // id 森林中无该 ID，或分区映射中无该 ID，则不处理该 ID
            if (!idMap.containsKey(id) || partitionMap.get(id) == null) {
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
        Long login = mediator.loginUserOrE();

        Partition p = mapper.selectOneByQuery(QueryWrapper.create()
                                                          .eq(Partition::getUserId, login)
                                                          .eq(Partition::getName, name));
        if (p == null) {
            return null;
        }
        SortedSet<PartitionVo> vos = getPartitionVoStructure(login, p.getId());
        return vos == null ? null : vos.first();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SortedSet<PartitionVo> addPartition(PartitionDto dto) throws Exception {
        Long login = mediator.loginUserOrE();
        // 验证字段
        // 暂未使用分区可见性验证，因为自动映射会处理
        VerifyTool.of(
                      // 验证分区名格式及是否已存在
                      PartitionVerifyNode.name(mediator)
                                         .login(login)
                                         .target(dto.getName()),
                      PartitionVerifyNode.id(mediator)
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
    public Boolean isUserPartitionExist(Long partitionId) throws Exception {
        return isUserPartitionExist(ServiceUtil.loginUserOrE(), partitionId);
    }

    @Override
    public Boolean isUserPartitionExist(Long userId, Long partitionId) throws Exception {
        mediator.loginUserOrE();

        if (BeanUtil.isAnyBlank(userId, partitionId)) {
            return false;
        }

        return mapper.selectCountByQuery(QueryWrapper.create()
                                                     .eq(Partition::getUserId, userId)
                                                     .eq(Partition::getId, partitionId)) > 0;
    }

    @Override
    public Boolean isUserPartitionNameExist(Long userId, String partitionName) {
        if (BeanUtil.isAnyBlank(userId, partitionName)) {
            return false;
        }
        return mapper.selectCountByQuery(QueryWrapper.create()
                                                     .eq(Partition::getUserId, userId)
                                                     .eq(Partition::getName, partitionName)) > 0;
    }

    @Override
    public Boolean isUserPartitionReachable(Long userId, Long partitionId) throws Exception {
        // 分区为当前登录用户的分区
        var login = ServiceUtil.loginUser();
        if (isUserPartitionExist(login, partitionId)) {
            return true;
        }
        // 分区权限为公开
        long count = mapper.selectCountByQuery(QueryWrapper.create()
                                                           .where(PARTITION.ID.eq(partitionId))
                                                           .and(PARTITION.VISIBILITY.eq(VisibilityEnum.PUBLIC)));

        return count == 1;
    }

    @Override
    public SortedSet<PartitionVo> updatePatch(PartitionUpdateDto dto) throws Exception {
        mediator.loginUserOrE();

        JsonNullable<Long> parentId = dto.getParentId();
        Long parentLong = JsonNullableUtil.getObjOrNull(parentId);

        JsonNullable<String> name = dto.getName();
        JsonNullable<VisibilityEnum> visibility = dto.getVisibility();
        JsonNullable<Long> order = dto.getOrder();

        Long id = dto.getId();
        Partition partition = getById(id);
        // 名字相同则不修改
        name = partition.getName()
                        .equals(name.orElse(null)) ? JsonNullable.undefined() : name;

        String nameString = JsonNullableUtil.getObjOrNull(name);

        VerifyTool.of(
                      PartitionVerifyNode.id(mediator)
                                         .target(id),
                      PartitionVerifyNode.id(mediator)
                                         .target(parentLong)
                                         .allowNull(true),
                      PartitionVerifyNode.name(mediator)
                                         .target(nameString)
                                         .allowNull(true),
                      new NotAllBlankVerifyNode(
                          parentLong,
                          nameString,
                          visibility,
                          order
                      ))
                  .doVerify();

        // 验证是否循环
        checkCirculate(dto.getId(), parentLong);

        // 构建更新链
        UpdateChain<Partition> chain = UpdateChain.of(Partition.class)
                                                  .where(PARTITION.ID.eq(id));

        // 分区名
        name.ifPresent(n -> chain.set(PARTITION.NAME, n, n != null));
        // 父分区
        parentId.ifPresent(p -> chain.set(PARTITION.PARENT_ID, p));
        // 可见性
        visibility.ifPresent(v -> chain.set(PARTITION.VISIBILITY, v, v != null));
        // 排序
        order.ifPresent(o -> chain.set(PARTITION.ORDER, o, o != null));

        if (!chain.update()) {
            throw new ServiceException(PartitionConstant.UPDATE_FAILED);
        }
        return getPartitionVos(dto.getId());
    }

    /**
     * 检查是否存在循环引用的情况。
     *
     * @param id       当前节点的ID
     * @param parentId 父节点的ID，如果为null则表示无父节点
     */
    private void checkCirculate(Long id, Long parentId) throws Exception {
        if (parentId == null) {
            return;
        }
        // 获取相关的所有 ID
        // 获取父分区的相关分区
        Set<Long> ids = getWithPartitionChildren(parentId);
        // 合并该分区的相关分区
        ids.addAll(getWithPartitionChildren(id));

        // 获取分区结构
        List<Partition> partitions = listByIds(ids);
        Map<Long, Set<Long>> idMap = getPartitionIdMapping(partitions).idMap();
        // 验证成环
        VerifyTool.of(PartitionVerifyNode.ID_LOOP.target(id)
                                                 .idMap(idMap)
                                                 .parent(parentId))
                  .doVerify();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SortedSet<PartitionVo> deleteOne(Long partitionId) throws Exception {
        // 验证 ID 是否存在
        Long login = mediator.loginUserOrE();
        if (!isUserPartitionExist(login, partitionId)) {
            throw new ServiceException(PartitionConstant.NOT_EXIST);
        }

        Partition partition = getById(partitionId);
        Long parentId = partition.getParentId();
        // 转移父类
        UpdateChain.of(Partition.class)
                   .where(PARTITION.PARENT_ID.eq(partitionId))
                   .set(PARTITION.PARENT_ID, parentId)
                   .update();
        // 移除当前类
        if (mapper.deleteById(partitionId) != 1) {
            throw ServiceException.processingFailed(PartitionConstant.DELETE_FAILED);
        }

        MqPartitionChangeDto dto = new MqPartitionChangeDto(Set.of(partitionId), parentId);
        dto.setUserId(login);
        mqArticleService.articlePartitionChange(dto);
        return getPartitionVos();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SortedSet<PartitionVo> deleteBatch(Long partitionId) throws Exception {
        // 验证 ID 是否存在
        Long login = mediator.loginUserOrE();
        if (!isUserPartitionExist(login, partitionId)) {
            throw new ServiceException(PartitionConstant.NOT_EXIST);
        }
        // 获取分区及其子分区 ID，批量删除
        Partition partition = getById(partitionId);
        Set<Long> ids = getWithPartitionChildren(partitionId);
        if (mapper.deleteBatchByIds(ids) != ids.size()) {
            throw ServiceException.processingFailed(PartitionConstant.DELETE_FAILED);
        }

        // 通知文章服务，修改归属分区
        MqPartitionChangeDto dto = new MqPartitionChangeDto(ids, partition.getParentId());
        dto.setUserId(login);
        mqArticleService.articlePartitionChange(dto);
        return getPartitionVos();
    }
}

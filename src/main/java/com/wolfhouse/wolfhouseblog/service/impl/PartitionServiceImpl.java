package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.auth.service.verify.VerifyTool;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.partition.PartitionVerifyNode;
import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.user.UserVerifyNode;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.PartitionMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Partition;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import com.wolfhouse.wolfhouseblog.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class PartitionServiceImpl extends ServiceImpl<PartitionMapper, Partition> implements PartitionService {
    private final UserAuthService authService;

    @Override
    public List<PartitionVo> getPartitionVos() throws Exception {
        Long login = ServiceUtil.loginUserOrE();
        VerifyTool.of(UserVerifyNode.id(authService)
                                    .target(login))
                  .doVerify();
        return getPartitionVoStructure(login);
    }

    @Override
    public List<PartitionVo> getPartitionVoStructure(Long userId) {
        return getPartitionVoStructure(userId, null);
    }

    @Override
    public List<PartitionVo> getPartitionVoStructure(Long userId, Long partitionId) {
        // TODO 优化逻辑，可以先获取所有父节点，再根据父节点获取其孩子节点
        // 获取用户的全部分区
        List<Partition> partitions = mapper.selectListByQuery(
             QueryWrapper.create()
                         .eq(Partition::getUserId, userId)
                         .eq(Partition::getId, partitionId, partitionId != null));

        Map<Long, PartitionVo> partitionMap = partitions.stream()
                                                        .collect(Collectors.toMap(
                                                             Partition::getId,
                                                             p -> BeanUtil.copyProperties(p, PartitionVo.class)));

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
                // 初始化孤独 ID
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

        // 从森林中去除孤独节点
        singleIds.forEach(idMap::remove);
        List<PartitionVo> singlesList = singleIds.stream()
                                                 .map(partitionMap::get)
                                                 .toList();

        // 遍历森林，构建 Vo 结构
        // 最大深度遍历
        List<PartitionVo> vos = new ArrayList<>(deepSearchPartitionVos(
             idMap,
             null,
             new HashSet<>(),
             partitionMap).values()
                          .stream()
                          .toList());
        vos.addAll(singlesList);
        return vos;
    }

    /**
     * 最大深度搜索，要组成一个父节点对应孩子节点的树。
     * 遍历每个节点，若节点有孩子，则会递归获取其所有子孙。
     *
     * @param ids   要搜索的 id 列表
     * @param idMap id 森林
     * @return 分区 Vo 列表
     */
    private Map<Long, PartitionVo> deepSearchPartitionVos(Map<Long, Set<Long>> idMap,
                                                          Set<Long> ids,
                                                          Set<Long> processed,
                                                          Map<Long, PartitionVo> partitionMap) {
        Map<Long, PartitionVo> vos = new HashMap<>(idMap.size());
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
                     partitionMap).values()
                                  .toArray(new PartitionVo[0]));
            }

            // 将自身添加至结果，表示已处理过
            vos.put(id, vo);
            processed.add(id);
        });

        return vos;
    }

    @Override
    public PartitionVo getPartitionVoByName(String name) {
        // TODO
        return null;
    }

    @Override
    public List<PartitionVo> addPartition(PartitionDto dto) {
        Long login = ServiceUtil.loginUserOrE();
        // 验证字段
        // 暂未使用分区可见性验证，因为自动映射会处理
        VerifyTool.of(
             UserVerifyNode.id(authService)
                           .target(login),
             // 验证分区名格式及是否已存在
             PartitionVerifyNode.name(this)
                                .target(dto.getName()),
             PartitionVerifyNode.id(this)
                                .target(dto.getParentId())
                                .allowNull(true));

        // TODO 添加分区
        return getPartitionVoStructure(login);
    }

    @Override
    public Boolean isUserPartitionExist(Long userId, Long partitionId) {
        return mapper.selectCountByQuery(QueryWrapper.create()
                                                     .eq(Partition::getUserId, userId)
                                                     .eq(Partition::getId, partitionId)) > 0;
    }
}

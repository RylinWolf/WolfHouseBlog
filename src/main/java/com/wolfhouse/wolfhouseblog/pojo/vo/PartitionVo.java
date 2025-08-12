package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author linexsong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartitionVo implements Comparable<PartitionVo> {
    private Long id;
    private String name;
    private VisibilityEnum visibility;
    private Long order;
    private PartitionVo[] children;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartitionVo that = (PartitionVo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(PartitionVo o) {
        int i = order.compareTo(o.getOrder());
        if (i != 0) {
            return i;
        }
        return id.compareTo(o.getId());
    }

}

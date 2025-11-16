package com.wolfhouse.wolfhouseblog.common.utils.result;

import com.mybatisflex.core.paginate.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Rylin Wolf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private Long pageNum;
    private Long pageSize;
    private Long total;
    private List<T> records;

    public static <E> PageResult<E> ofPage(Page<E> page) {
        return new PageResult<>(page.getPageNumber(),
                                page.getPageSize(),
                                page.getTotalRow(),
                                page.getRecords());
    }
}

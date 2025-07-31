package com.wolfhouse.wolfhouseblog.common.utils.page;

import com.mybatisflex.core.paginate.Page;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author linexsong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private Long currentPage;
    private Long totalPage;
    private Long totalRow;


    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> res = new PageResult<>();
        res.setRecords(page.getRecords());
        res.setCurrentPage(page.getPageNumber());
        res.setTotalPage(page.getTotalPage());
        res.setTotalRow(page.getTotalRow());
        return res;
    }

    public static <T, E> PageResult<E> of(Page<T> page, Class<E> clazz) {
        List<E> records = BeanUtil.copyList(page.getRecords(), clazz);
        return of(new Page<>(
                records,
                page.getPageNumber(),
                page.getPageSize(),
                page.getTotalRow()));
    }

}

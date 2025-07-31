package com.wolfhouse.wolfhouseblog.common.utils.page;

import com.mybatisflex.core.paginate.Page;
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


    private static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> res = new PageResult<>();
        res.setRecords(page.getRecords());
        res.setCurrentPage(page.getPageNumber());
        res.setTotalPage(page.getTotalPage());
        res.setTotalRow(page.getTotalRow());
        return res;
    }
}

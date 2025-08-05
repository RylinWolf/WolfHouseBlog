package com.wolfhouse.wolfhouseblog.common.utils.page;

import lombok.Data;

/**
 * @author linexsong
 */
@Data
public class PageDto {
    protected Long pageNumber = 1L;
    protected Long pageSize = 10L;

}

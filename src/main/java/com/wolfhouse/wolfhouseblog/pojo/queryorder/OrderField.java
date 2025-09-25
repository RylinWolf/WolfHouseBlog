package com.wolfhouse.wolfhouseblog.pojo.queryorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderField {
    private String field;
    private Boolean isAsc = true;
    private String missing = "_last";

    public OrderField(String field, Boolean isAsc) {
        this.field = field;
        this.isAsc = isAsc;
    }

    public OrderField(String field) {
        this.field = field;
    }
}

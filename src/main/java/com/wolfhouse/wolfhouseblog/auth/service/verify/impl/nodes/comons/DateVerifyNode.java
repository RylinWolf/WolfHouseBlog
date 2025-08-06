package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.comons;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;

import java.time.LocalDateTime;

/**
 * @author linexsong
 */
public class DateVerifyNode extends BaseVerifyNode<LocalDateTime> {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public DateVerifyNode(LocalDateTime t, LocalDateTime start, LocalDateTime end) {
        this.t = t;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean verify() {
        return super.verify() && t.isAfter(start) && t.isBefore(end) || t.isEqual(start) || t.isEqual(end);
    }
}

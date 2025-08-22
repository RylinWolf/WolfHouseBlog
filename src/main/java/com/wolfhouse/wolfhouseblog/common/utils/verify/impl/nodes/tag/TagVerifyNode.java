package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.tag;

import com.wolfhouse.wolfhouseblog.service.TagService;

/**
 * @author linexsong
 */
public class TagVerifyNode {
    public static final TagNameVerifyNode NAME = new TagNameVerifyNode();

    private static TagIdVerifyNode ID;

    public static TagIdVerifyNode id(TagService service) {
        if (ID == null) {
            ID = new TagIdVerifyNode(service);
        }
        return ID;
    }
}

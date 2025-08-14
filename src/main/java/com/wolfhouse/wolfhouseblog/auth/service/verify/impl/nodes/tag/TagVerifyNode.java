package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.tag;

import com.wolfhouse.wolfhouseblog.service.TagService;

/**
 * @author linexsong
 */
public class TagVerifyNode {
    private static TagIdVerifyNode ID;

    public static TagIdVerifyNode id(TagService service) {
        if (ID == null) {
            ID = new TagIdVerifyNode(service);
        }
        return ID;
    }
}

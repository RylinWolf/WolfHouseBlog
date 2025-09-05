package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.file;

import com.mybatisflex.core.query.QueryWrapper;
import com.wolfhouse.wolfhouseblog.common.utils.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.service.OssChunkFileService;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.OssChunkFileTableDef.OSS_CHUNK_FILE;

/**
 * @author linexsong
 */
public class UploadIdExistVerifyNode extends BaseVerifyNode<String> {
    private final OssChunkFileService service;

    public UploadIdExistVerifyNode(OssChunkFileService service) {
        this.service = service;
    }

    @Override
    public boolean verify() {
        return service.exists(QueryWrapper.create()
                                          .where(OSS_CHUNK_FILE.UPLOAD_ID.eq(this.t)));
    }
}

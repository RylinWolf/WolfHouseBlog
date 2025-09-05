package com.wolfhouse.wolfhouseblog.common.utils.verify.impl.nodes.file;

import com.wolfhouse.wolfhouseblog.service.file.OssChunkFileService;

/**
 * @author linexsong
 */
public class FileUploadVerifyNode {
    private static UploadIdExistVerifyNode UPLOAD_ID_EXIST;

    public static UploadIdExistVerifyNode uploadIdExist(OssChunkFileService service) {
        if (UPLOAD_ID_EXIST == null) {
            UPLOAD_ID_EXIST = new UploadIdExistVerifyNode(service);
        }
        return UPLOAD_ID_EXIST;
    }
}

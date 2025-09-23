package com.wolfhouse.wolfhouseblog.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author linexsong
 */
public interface FileService {
    /**
     * 上传用户头像文件
     *
     * @param file 要上传的头像文件
     * @return 上传成功后的头像文件访问URL
     */
    String uploadAvatar(MultipartFile file);
}

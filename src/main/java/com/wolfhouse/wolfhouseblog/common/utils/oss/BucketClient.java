package com.wolfhouse.wolfhouseblog.common.utils.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author Rylin Wolf
 */
@Data
public class BucketClient {
    private final OSSClient ossClient;
    private final String bucketName;
    /** 默认的存储路径前缀 */
    public String dirPrefix;
    public String customEndpoint;

    public BucketClient(OSSClient ossClient, String bucketName) {
        this(ossClient, bucketName, "");
    }

    public BucketClient(OSSClient ossClient, String bucketName, String dirPrefix) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
        this.dirPrefix = dirPrefix;
    }

    // region 静态方法

    /**
     * 防止覆盖已存在的对象，将请求的元数据中设置禁止覆盖的标志。
     *
     * @param req 要处理的 {@code PutObjectRequest} 对象，表示上传请求。
     */
    public static void forbidOverwrite(PutObjectRequest req) {
        ObjectMetadata metadata = req.getMetadata();
        if (metadata == null) {
            metadata = new ObjectMetadata();
            req.setMetadata(metadata);
        }
        metadata.setHeader("x-oss-forbid-overwrite", "true");
    }

    private String buildPath(String filename) {
        return Path.of(dirPrefix, filename)
                   .toString();
    }

    // endregion

    // region equals and hashcode

    @Override
    public boolean equals(Object obj) {
        if (!BucketClient.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        BucketClient client = (BucketClient) obj;
        return this.bucketName.equals(client.getBucketName()) && this.ossClient.equals(client.getOssClient());
    }

    @Override
    public int hashCode() {
        return this.bucketName.hashCode() + this.ossClient.hashCode();
    }

    // endregion

    /**
     * 获取文件上传路径。
     * <p>
     * 构建文件在存储服务中的完整上传路径，格式为：[自定义域名|桶名.域名]/目录前缀/文件名。
     * 如果提供了自定义域名，则优先使用自定义域名；否则，使用桶名及默认域名。
     *
     * @param filename 文件名称，用于标识上传的目标文件
     * @return 构建后的完整文件上传路径
     */
    public String getFileUploadPath(String filename) {
        // [自定义域名|桶名.域名]/目录前缀/文件名
        String endpoint = customEndpoint == null ? String.format("%s.%s", getBucketName(),
                                                                 ossClient.getEndpoint()
                                                                          .getHost())
                                                 : customEndpoint;

        return Path.of(endpoint, getDirPrefix(), filename)
                   .toString();
    }


    /**
     * 上传指定的对象到存储桶中。
     * <p>
     * <b>注意，使用该方法时，dirPrefix 属性将不会生效</b>
     *
     * @param req 包含上传请求信息的 {@code PutObjectRequest} 对象，包括目标存储桶名称、对象键及文件内容等
     * @return 返回存储桶完成对象上传后的结果信息 {@code PutObjectResult}
     */
    public PutObjectResult putObject(PutObjectRequest req) {
        return ossClient.putObject(req);
    }

    /**
     * 将本地文件上传到指定的存储桶中。
     *
     * @param filename 文件在存储桶中的目标路径（包含文件名称）
     * @param file     要上传的本地文件
     * @return 返回存储桶完成文件上传后的结果信息
     */
    public PutObjectResult putFile(String filename, File file, boolean overwrite) {
        PutObjectRequest req = new PutObjectRequest(bucketName, buildPath(filename), file);
        if (overwrite) {
            forbidOverwrite(req);
        }
        return ossClient.putObject(req);
    }

    public PutObjectResult putFile(String filename, File file) {
        return putFile(filename, file, false);
    }

    /**
     * 将输入流形式的文件上传到指定的存储桶中。
     *
     * @param filename 文件在存储桶中的目标路径（包含文件名称）
     * @param ins      要上传的输入流
     * @param acl      文件访问权限
     * @return 返回存储桶完成文件上传后的结果信息
     */
    public PutObjectResult putStream(String filename, InputStream ins, boolean overwrite, CannedAccessControlList acl) {
        PutObjectRequest req = new PutObjectRequest(bucketName, buildPath(filename), ins);
        if (overwrite) {
            forbidOverwrite(req);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectAcl(acl);
        req.setMetadata(metadata);
        return ossClient.putObject(req);
    }

    public PutObjectResult putStream(String filename, InputStream ins) {
        return putStream(filename, ins, false, CannedAccessControlList.Private);
    }

    /**
     * 将字节数组形式的文件上传到指定的存储桶中。默认权限为私有
     *
     * @param filename 文件在存储桶中的目标路径（包含文件名称）
     * @param bytes    要上传的字节数组
     * @return 返回存储桶完成文件上传后的结果信息
     */
    public PutObjectResult putBytes(String filename, byte[] bytes, boolean overwrite) {
        return putStream(filename, new ByteArrayInputStream(bytes), overwrite, CannedAccessControlList.Private);
    }

    public PutObjectResult putBytes(String filename, byte[] bytes) {
        return putBytes(filename, bytes, false);
    }

    /**
     * 从存储桶中删除指定的对象。
     *
     * @param filename 要删除的对象在存储桶中的路径（包含文件名称）
     * @return 返回删除操作的结果 {@code VoidResult}
     */
    public VoidResult deleteObject(String filename) {
        return ossClient.deleteObject(bucketName, buildPath(filename));
    }

    /**
     * 检查指定名称的对象是否存在于存储桶中。
     *
     * @param filename 要检查的对象在存储桶中的路径（包含文件名称）
     * @return 如果对象存在则返回 {@code true}，否则返回 {@code false}
     */
    public boolean doesObjectExist(String filename) {
        return ossClient.doesObjectExist(bucketName, buildPath(filename));
    }

    /**
     * 设置指定对象的访问控制列表（ACL）。
     *
     * @param filename 对象在存储桶中的路径（包含文件名称）
     * @param acl      要设置的访问控制列表，使用 {@code CannedAccessControlList} 枚举值
     */
    public void setObjectAcl(String filename, CannedAccessControlList acl) {
        ossClient.setObjectAcl(bucketName, buildPath(filename), acl);
    }

    /**
     * 将指定的对象设置为公共读取权限。
     *
     * @param filename 要设置权限的对象在存储桶中的路径（包含文件名称）
     */
    public void setObjectPublicRead(String filename) {
        setObjectAcl(filename, CannedAccessControlList.PublicRead);
    }


}

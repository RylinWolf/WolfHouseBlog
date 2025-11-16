package com.wolfhouse.wolfhouseblog.common.utils.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.Bucket;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Oss 工具客户端
 * <p>
 * 在不使用之后，需要通过 shutdown 方法关闭客户端！
 *
 * @author Rylin Wolf
 */
@Data
public class OssUtilClient {
    private final OSSClient ossClient;
    public String dirPrefix = "";
    /** 缓存已获得过的桶 */
    private Map<String, BucketClient> cachedBuckets = new ConcurrentHashMap<>(5);

    // region 初始化、销毁

    public OssUtilClient(String accessKeyId,
                         String accessKeySecret,
                         String region,
                         String endpoint) {
        // 创建凭证提供者
        DefaultCredentialProvider provider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // 配置客户端参数
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 显式声明使用V4签名算法
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        clientBuilderConfiguration.setSupportCname(true);
        clientBuilderConfiguration.setProtocol(Protocol.HTTPS);

        // 初始化OSS客户端
        this.ossClient = (OSSClient) OSSClientBuilder.create()
                                                     .credentialsProvider(provider)
                                                     .clientConfiguration(clientBuilderConfiguration)
                                                     .region(region)
                                                     .endpoint(endpoint)
                                                     .build();
    }

    public OssUtilClient(OSSClient ossClient) {
        this.ossClient = ossClient;
    }

    /**
     * 在不使用客户端之后，需要进行销毁
     */
    public void shutdown() {
        ossClient.shutdown();
    }

    // endregion

    // region 操作方法

    /**
     * 列举当前账号下的所有存储桶（Bucket）。
     *
     * @return 返回包含当前账号所有存储桶信息的列表
     */
    public List<Bucket> listBuckets() {
        return ossClient.listBuckets();
    }

    /**
     * 获取指定名称的存储桶实例。如果缓存中尚不存在该存储桶实例，则新创建一个并缓存。
     *
     * @param bucketName 存储桶的名称
     */
    public BucketClient getBucket(String bucketName) {
        return cachedBuckets.computeIfAbsent(bucketName, (n) -> new BucketClient(ossClient, n, dirPrefix));
    }

    /**
     * 获取所有已缓存的存储桶名称。
     *
     * @return 返回一个包含所有已缓存存储桶名称的集合
     */
    public Set<String> getCachedBucketNames() {
        return cachedBuckets.keySet();
    }

    /**
     * 从缓存中删除指定名称的存储桶对象。
     *
     * @param bucketName 要删除的存储桶名称
     * @return 返回被删除的 {@code BucketClient} 对象，如果缓存中不存在该名称的存储桶，则返回 {@code null}
     */
    public BucketClient removeCachedBucket(String bucketName) {
        return cachedBuckets.remove(bucketName);
    }

    // endregion
}

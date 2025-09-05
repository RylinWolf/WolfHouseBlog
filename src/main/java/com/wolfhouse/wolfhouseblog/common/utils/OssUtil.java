package com.wolfhouse.wolfhouseblog.common.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.io.BoundedInputStream;
import com.aliyun.sdk.service.oss2.models.*;
import com.aliyun.sdk.service.oss2.signer.SignerV4;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.properties.OssProperties;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ClientChunkFileCompleteDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ClientChunkFileUploadDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.ChunkFilePermitVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadCompleteVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OSS(对象存储服务)工具类，用于处理文件上传、分片上传等OSS相关操作
 * 提供了初始化上传、分片上传、完成上传和中止上传等功能
 *
 * @author rylinwolf
 */
@Component
@RequiredArgsConstructor
public class OssUtil {
    private final OssProperties properties;

    /**
     * OSS客户端持有者，使用单例模式管理OSS客户端实例
     * 负责OSS客户端的延迟初始化和实例管理
     */
    private static class ClientHolder {
        private static OSSClient CLIENT;

        static OSSClient instance(OssProperties properties) {
            if (CLIENT == null) {
                CLIENT = OSSClient.newBuilder()
                                  // 从环境中读取权限配置
                                  .credentialsProvider(new EnvironmentVariableCredentialsProvider())
                                  // 设置 Bucket 地址
                                  .region(properties.region())
                                  .signer(new SignerV4())
                                  .build();
            }
            return CLIENT;
        }
    }

    /**
     * 获取OSS客户端实例
     * 如果客户端未初始化，将创建新的客户端实例
     *
     * @return 返回OSS客户端实例
     */
    public OSSClient getClient() {
        return ClientHolder.instance(properties);
    }

    /**
     * 关闭OSS客户端连接并清理资源
     * 会将客户端实例设置为null，便于垃圾回收
     *
     * @throws ServiceException 当关闭客户端发生异常时抛出
     */
    public void shutdownClient() {
        try {
            // 关闭客户端
            getClient().close();
            // 初始化客户端为 null
            ClientHolder.CLIENT = null;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 初始化分片上传操作，向服务端发起分片上传的启动请求并返回上传许可信息。
     *
     * @param dto 包含上传对象名称的参数数据传输对象（ClientChunkFileUploadInitialDto）
     * @return 返回包含上传ID和状态码的分片上传许可对象（ChunkFilePermitVo）
     */
    public ChunkFilePermitVo initialChunkUpload(ClientChunkFileUploadDto dto) {
        var client = getClient();
        var initialResult = client.initiateMultipartUpload(
            InitiateMultipartUploadRequest.newBuilder()
                                          .bucket(properties.bucket())
                                          .key(dto.getObjectName())
                                          .build());
        // 获取 上传 ID
        var uploadId = initialResult.initiateMultipartUpload()
                                    .uploadId();
        var statusCode = initialResult.statusCode();

        return new ChunkFilePermitVo(uploadId, statusCode);
    }

    /**
     * 执行文件分片上传操作，将客户端传递的分片文件上传到指定存储服务。
     *
     * @param dto 包含分片上传所需参数的传输对象，包括上传对象名称、文件流、分片号、上传ID以及内容MD5校验值
     * @return 返回分片上传操作的结果对象，包含分片标记（eTag）、分片号以及状态码
     * @throws ServiceException 如果上传过程中发生IO异常或其他错误，将抛出此自定义运行时异常
     */
    public FileUploadResultVo chunkUpload(ClientChunkFileUploadDto dto) {
        var client = getClient();

        try (BoundedInputStream bis = new BoundedInputStream(dto.getIns())) {
            var reqBuilder = UploadPartRequest.newBuilder()
                                              // bucket 名
                                              .bucket(properties.bucket())
                                              // objectName
                                              .key(dto.getObjectName())
                                              // 上传 Id
                                              .uploadId(dto.getUploadId())
                                              // 分片号
                                              .partNumber(dto.getPartNumber())
                                              // 上传内容
                                              .body(BinaryData.fromStream(bis));
            // 进行 MD5 校验
            if (dto.getContentMd5() != null) {
                // 内容 MD5 校验
                reqBuilder.contentMD5(dto.getContentMd5());
            }

            // 上传分片
            var res = client.uploadPart(reqBuilder.build());

            return new FileUploadResultVo(res.eTag(),
                                          dto.getPartNumber(),
                                          res.statusCode());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 完成文件分片上传操作，将已上传的所有分片合并为一个完整的文件。
     *
     * @param dto 包含文件分片上传完成信息的传输对象，包含上传对象名称、上传ID以及已上传分片的eTags
     * @return 返回文件上传完成后的结果对象，包含处理状态码
     */
    public FileUploadCompleteVo completeChunkUpload(ClientChunkFileCompleteDto dto) {
        var client = getClient();
        var requestBuilder = CompleteMultipartUploadRequest.newBuilder()
                                                           .bucket(properties.bucket())
                                                           .key(dto.getObjectName())
                                                           .uploadId(dto.getUploadId());
        if (dto.getETags()
               .isEmpty()) {
            // 未传递已上传的 eTags，则自动构建
            requestBuilder.header("x-oss-complete-all", "yes");
        } else {
            // 指定 eTags
            requestBuilder.completeMultipartUpload(
                CompleteMultipartUpload.newBuilder()
                                       .parts(dto.getETags())
                                       .build());
        }
        var completeResult = client.completeMultipartUpload(requestBuilder.build());

        return new FileUploadCompleteVo(completeResult.statusCode());
    }

    /**
     * 中止分片上传操作，向存储服务发送中止上传请求并返回结果状态。
     *
     * @param dto 包含分片上传相关参数的传输对象，包括上传 ID 和对象名
     * @return 返回包含中止操作后状态码的结果对象（FileUploadCompleteVo）
     */
    public FileUploadCompleteVo abortChunkUpload(ClientChunkFileUploadDto dto) {
        var client = getClient();
        var abortResult = client.abortMultipartUpload(
            AbortMultipartUploadRequest.newBuilder()
                                       .uploadId(dto.getUploadId())
                                       .bucket(properties.bucket())
                                       .key(dto.getObjectName())
                                       .build());
        return new FileUploadCompleteVo(abortResult.statusCode());

    }
}

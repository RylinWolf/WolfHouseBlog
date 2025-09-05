package com.wolfhouse.wolfhouseblog.common.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.io.BoundedInputStream;
import com.aliyun.sdk.service.oss2.models.InitiateMultipartUploadRequest;
import com.aliyun.sdk.service.oss2.models.UploadPartRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.properties.OssProperties;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ClientChunkFileUploadDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.ChunkFilePermitVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author linexsong
 */
@Component
@RequiredArgsConstructor
public class OssUtil {
    private final OssProperties properties;

    private static class ClientHolder {
        private static OSSClient CLIENT;

        static OSSClient instance(OssProperties properties) {
            if (CLIENT == null) {
                CLIENT = OSSClient.newBuilder()
                                  // 从环境中读取权限配置
                                  .credentialsProvider(new EnvironmentVariableCredentialsProvider())
                                  // 设置 Bucket 地址
                                  .region(properties.region())
                                  .build();
            }
            return CLIENT;
        }
    }

    public OSSClient getClient() {
        return ClientHolder.instance(properties);
    }

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
}

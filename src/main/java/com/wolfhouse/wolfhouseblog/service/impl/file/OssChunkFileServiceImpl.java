package com.wolfhouse.wolfhouseblog.service.impl.file;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.mapper.file.OssChunkFileMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.file.OssChunkFile;
import com.wolfhouse.wolfhouseblog.service.file.OssChunkFileService;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
public class OssChunkFileServiceImpl extends ServiceImpl<OssChunkFileMapper, OssChunkFile> implements OssChunkFileService {
}

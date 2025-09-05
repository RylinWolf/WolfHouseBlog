package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.mapper.OssChunkFileMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.OssChunkFile;
import com.wolfhouse.wolfhouseblog.service.OssChunkFileService;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
public class OssChunkFileServiceImpl extends ServiceImpl<OssChunkFileMapper, OssChunkFile> implements OssChunkFileService {
}

package com.wolfhouse.wolfhouseblog.service.impl.file;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.mapper.file.OssChunkFilePartMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.file.OssChunkFilePart;
import com.wolfhouse.wolfhouseblog.service.file.OssChunkFilePartService;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
public class OssChunkFilePartImpl extends ServiceImpl<OssChunkFilePartMapper, OssChunkFilePart> implements OssChunkFilePartService {
}

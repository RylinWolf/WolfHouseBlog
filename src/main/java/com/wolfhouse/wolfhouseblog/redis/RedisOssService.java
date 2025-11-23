package com.wolfhouse.wolfhouseblog.redis;

import com.wolfhouse.wolfhouseblog.common.constant.OssRedisConstant;
import com.wolfhouse.wolfhouseblog.service.OssUploadLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author Rylin Wolf
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisOssService {
    private final ServiceRedisUtil redisUtil;
    private final OssUploadLogService ossLogService;

    public Boolean addDuplicateAvatarFromOss(Long userId, Set<String> except) {
        Set<String> filePaths = ossLogService.getUndeletedAvatarOssPath(userId);
        filePaths.removeAll(except);
        return this.addDuplicateAvatar(filePaths) == filePaths.size();
    }

    public Long addDuplicateAvatar(Set<String> avatarPath) {
        return redisUtil.addSetValue(OssRedisConstant.OSS_DELETE_SET, (Object[]) avatarPath.toArray(new String[0]));
    }

    public Set<String> getCachedDuplicateAvatar() {
        Set<Object> deletes = redisUtil.getSetMembers(OssRedisConstant.OSS_DELETE_SET);
        return deletes.stream()
                      .map(String::valueOf)
                      .collect(java.util.stream.Collectors.toSet());
    }

    public void removeAllAvatar(Set<String> deletedObject) {
        redisUtil.removeSetValue(OssRedisConstant.OSS_DELETE_SET, (Object[]) deletedObject.toArray(new String[0]));
    }
}

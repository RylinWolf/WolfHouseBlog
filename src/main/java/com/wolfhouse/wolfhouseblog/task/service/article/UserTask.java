package com.wolfhouse.wolfhouseblog.task.service.article;

import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.oss.BucketClient;
import com.wolfhouse.wolfhouseblog.redis.RedisOssService;
import com.wolfhouse.wolfhouseblog.service.OssUploadLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 用户相关定时任务
 *
 * @author Rylin Wolf
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserTask {
    public final OssUploadLogService ossUploadLogService;
    public final RedisOssService redisOssService;
    public final BucketClient bucketClient;

    @Scheduled(cron = "0 30 3 */4 * *")
    public void fetchDuplicateAvatar() {
        log.info("正在扫描 OSS 表，缓存用户头像重复上传列表...");
        // 获取重复上传的头像数据，该数据不包括最近一条上传的内容
        Set<String> duplicates = ossUploadLogService.getDuplicateAvatarOssPath();
        // 添加到删除列表
        Long amount = redisOssService.addDuplicateAvatar(duplicates);
        if (amount != duplicates.size()) {
            log.warn("缓存的头像数量与扫描到的数量不一致: {} of all {}\n{}", amount, duplicates.size(), duplicates);
        }
        log.debug("用户头像重新上传列表扫描完毕");
    }

    @Scheduled(cron = "0 30 4 */2 * *")
    public void doCleanCachedDuplicateAvatar() {
        log.info("正在执行清除已缓存的重复头像...");
        // 初始化保存所有 OSS 删除了的或不存在的文件
        ConcurrentSkipListSet<String> deletedObject = new ConcurrentSkipListSet<>();
        List<Thread> threads = new ArrayList<>();
        // 获取缓存内容
        Set<String> duplicates = redisOssService.getCachedDuplicateAvatar();
        // 调用 OSS 工具执行删除
        for (String duplicate : duplicates) {
            // 使用虚拟线程
            threads.add(Thread.ofVirtual()
                              .start(() -> {
                                  String thisFilename = duplicate;
                                  if (duplicate.startsWith(bucketClient.dirPrefix)) {
                                      thisFilename = new File(duplicate).getName();
                                  }
                                  if (!bucketClient.doesObjectExist(thisFilename)) {
                                      log.warn("[清除缓存的重复头像] OSS：该文件不存在: [{}]", thisFilename);
                                  } else {
                                      log.debug("[清除缓存的重复头像] OSS：执行删除: [{}]", thisFilename);
                                      bucketClient.deleteObject(thisFilename);
                                  }
                                  deletedObject.add(duplicate);
                                  int removedAmount = ossUploadLogService.removeByOssPath(duplicate);
                                  if (removedAmount != 1) {
                                      log.warn("[清除缓存的重复头像] OSS：删除日志失败: {} 条-{}",
                                               removedAmount,
                                               duplicate);
                                  }
                              }));
        }

        // 等待线程执行完毕
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new ServiceException(e.getMessage());
            }
        });
        if (deletedObject.isEmpty()) {
            log.debug("[清除缓存的重复头像] 没有要清除的对象");
            return;
        }
        redisOssService.removeAllAvatar(deletedObject);
        Set<String> shouldNull = redisOssService.getCachedDuplicateAvatar();
        if (shouldNull != null && !shouldNull.isEmpty()) {
            log.warn("[清除缓存的重复头像] 注意，仍有未清空的头像缓存");
        }
        log.info("[清除缓存的重复头像] 共清除记录: {} 条，详情开启 debug 查看", deletedObject.size());
        log.debug("[清除缓存的重复头像] 清除记录详情: {}", deletedObject);
    }
}

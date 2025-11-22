package com.wolfhouse.wolfhouseblog.common.utils.imageutil;

/**
 * @author Rylin Wolf
 */
public interface ImgValidConstant {
    String EMPTY_FILE = "文件为空";
    String INVALID_FILE_SIZE = "文件大小无效";
    String FILE_SIZE_MUST_LESS_THAN = "文件大小必须小于 [%s] ";

    String CONTENT_TYPE_NOT_ALLOWED = "不允许的媒体类型";
    String FORMAT_NOT_ALLOWED = "不允许的图片类型";

    String IMG_SCALE_MUST_LESS_THAN = "图片尺寸必须小于: (%s, %s) ";
    String IMG_SCALE_INVALID = "无效的图片尺寸";
    String IMG_PIXELS_OVER = "图片像素过大";
}

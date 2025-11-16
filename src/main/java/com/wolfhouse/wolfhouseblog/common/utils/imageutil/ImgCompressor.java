package com.wolfhouse.wolfhouseblog.common.utils.imageutil;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Rylin Wolf
 */
public class ImgCompressor {
    private Thumbnails.Builder<?> builder;

    public static ByteArrayOutputStream compressImage(InputStream input,
                                                      int maxWidth,
                                                      int maxHeight,
                                                      float quality,
                                                      String format) throws IOException {
        try (var output = new ByteArrayOutputStream()) {
            Thumbnails.of(input)
                      // 限制最大宽高，等比缩放
                      .size(maxWidth, maxHeight)
                      // 0~1，越小压缩越狠
                      .outputQuality(quality)
                      // 可选："jpg"、"png"、"webp" 等
                      .outputFormat(format)
                      .toOutputStream(output);
            return output;
        }
    }

    public static ByteArrayOutputStream compressImage(File file,
                                                      int maxWidth,
                                                      int maxHeight,
                                                      float quality,
                                                      String format) throws IOException {
        try (var output = new ByteArrayOutputStream()) {
            Thumbnails.of(file)
                      // 限制最大宽高，等比缩放
                      .size(maxWidth, maxHeight)
                      // 0~1，越小压缩越狠
                      .outputQuality(quality)
                      // 可选："jpg"、"png"、"webp" 等
                      .outputFormat(format)
                      .toOutputStream(output);
            return output;
        }
    }

    public static ImgCompressor of(InputStream input) {
        ImgCompressor instance = new ImgCompressor();
        instance.builder = Thumbnails.of(input);
        return instance;
    }

    public static ImgCompressor of(File file) {
        ImgCompressor instance = new ImgCompressor();
        instance.builder = Thumbnails.of(file);
        return instance;
    }

    public ImgCompressor scale(int maxWidth, int maxHeight) {
        builder.size(maxWidth, maxHeight);
        return this;
    }

    public ImgCompressor quality(float quality) {
        builder.outputQuality(quality);
        return this;
    }

    public ImgCompressor format(String format) {
        builder.outputFormat(format);
        return this;
    }

    /**
     * 执行图像压缩操作并返回压缩后的结果。
     * 此方法基于先前构建的压缩参数生成图像。
     *
     * @return 压缩后的图像，以 {@code BufferedImage} 对象形式返回。
     * @throws IOException 当图像压缩过程中发生 I/O 错误时抛出。
     */
    public ByteArrayOutputStream doCompress() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            builder.toOutputStream(outputStream);
            builder = null;
            return outputStream;
        }
    }
}

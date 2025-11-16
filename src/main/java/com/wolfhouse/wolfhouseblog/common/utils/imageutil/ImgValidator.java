package com.wolfhouse.wolfhouseblog.common.utils.imageutil;


import lombok.Data;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 图片验证器
 *
 * @author Rylin Wolf
 */
@Data
public class ImgValidator {
    private static final long IMAGE_MAX_SIZE = 3 * 1024 * 1024;
    private static final int IMAGE_MAX_WIDTH = 2000;
    private static final int IMAGE_MAX_HEIGHT = 2000;
    /** 默认最大 100 万像素 */
    private static final long MAX_PIXELS = 1_000_000L;
    private static final Set<String> ALLOWED_FORMATS = Set.of("JPEG", "PNG", "GIF", "WEBP");
    private static final String CONTENT_TYPE_PREFIX = "image/";

    protected long maxSize;
    protected int maxWidth;
    protected int maxHeight;
    protected long maxPixels;
    protected Set<String> allowedFormats;
    protected String contentTypePrefix;

    /**
     * ImgValidator 是一个用于校验图片文件合法性的工具类。支持校验图片的大小、宽高、像素总数、
     * 格式以及文件的媒体类型等属性，适用于图片上传前的格式检查。
     *
     * @param maxSize           允许的图片文件最大大小（单位：字节）。当值小于 0 时，使用默认值 {@code IMAGE_MAX_SIZE}。值为 0 时不校验。
     * @param maxWidth          图片的最大允许宽度（单位：像素）。当值小于 0 时，使用默认值 {@code IMAGE_MAX_WIDTH}。值为 0 时不校验。
     * @param maxHeight         图片的最大允许高度（单位：像素）。当值小于 0 时，使用默认值 {@code IMAGE_MAX_HEIGHT}。值为 0 时不校验。
     * @param maxPixels         图片的最大允许总像素数。当值小于 0 时，使用默认值 {@code MAX_PIXELS}。值为 0 时不校验。
     * @param allowedFormats    允许的图片格式集合。当为 {@code null} 或为空集合时，使用默认值 {@code ALLOWED_FORMATS}。
     * @param contentTypePrefix 文件的媒体类型前缀，用于限定允许的媒体类型。当为 {@code null} 时，使用默认值 {@code CONTENT_TYPE_PREFIX}。
     */
    public ImgValidator(long maxSize,
                        int maxWidth,
                        int maxHeight,
                        long maxPixels,
                        Set<String> allowedFormats,
                        String contentTypePrefix) {
        this.maxSize = maxSize < 0 ? IMAGE_MAX_SIZE : maxSize;
        this.maxWidth = maxWidth < 0 ? IMAGE_MAX_WIDTH : maxWidth;
        this.maxHeight = maxHeight < 0 ? IMAGE_MAX_HEIGHT : maxHeight;
        this.maxPixels = maxPixels < 0 ? MAX_PIXELS : maxPixels;
        this.allowedFormats = allowedFormats == null || allowedFormats.isEmpty() ? ALLOWED_FORMATS : allowedFormats;
        this.contentTypePrefix =
            contentTypePrefix == null ? CONTENT_TYPE_PREFIX : contentTypePrefix;
    }

    public ImgValidator() {
        this(IMAGE_MAX_SIZE, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT, MAX_PIXELS, ALLOWED_FORMATS, CONTENT_TYPE_PREFIX);
    }

    /**
     * 校验上传文件的媒体类型是否符合要求。
     *
     * @param file 上传的文件，类型为 {@code MultipartFile}，不能为 {@code null} 或空。
     *             方法会校验文件的媒体类型是否与指定的前缀相符。
     * @throws ImgValidException 如果文件的媒体类型为 {@code null}，或文件的媒体类型不符合指定的前缀。
     */
    protected void validContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(this.contentTypePrefix)) {
            throw new ImgValidException(ImgValidConstant.CONTENT_TYPE_NOT_ALLOWED);
        }
    }

    /**
     * 校验上传文件的大小是否符合要求。
     *
     * @param file 上传的文件，类型为 {@code MultipartFile}，不能为 {@code null} 或空。
     * @return 校验结果，包含文件大小及校验是否通过的信息，返回类型为 {@code Result}。
     * @throws ImgValidException 如果文件为空、文件大小无效，或文件大小超过允许的最大值。
     */
    protected long validSize(MultipartFile file) throws ImgValidException {
        if (file == null || file.isEmpty()) {
            throw new ImgValidException(ImgValidConstant.EMPTY_FILE);
        }

        long size = file.getSize();
        if (size <= 0) {
            throw new ImgValidException(ImgValidConstant.INVALID_FILE_SIZE);
        }
        if (maxSize > 0 && size > maxSize) {
            throw new ImgValidException(ImgValidConstant.FILE_SIZE_MUST_LESS_THAN.formatted(maxSize + "字节"));
        }
        return size;
    }

    /**
     * 校验图片的宽高尺寸是否合法。
     * 判断图片的宽度和高度是否为正数，是否不超过最大允许尺寸，
     * 且图片的总像素是否超出限制。
     *
     * @param width  图片的宽度信息
     * @param height 图片的高度信息
     * @return 返回一个包含图片宽度和高度的 {@code LinkedHashSet<Integer>}，如果校验通过。
     * @throws ImgValidException 如果图片尺寸超出最大宽高限制，或像素总数超出最大允许值。
     */
    protected ArrayList<Integer> validScale(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new ImgValidException(ImgValidConstant.IMG_SCALE_INVALID);
        }

        if (maxWidth > 0 && width > maxWidth) {
            throw new ImgValidException(ImgValidConstant.IMG_SCALE_MUST_LESS_THAN.formatted(maxWidth, maxHeight));
        }

        if (maxHeight > 0 && height > maxHeight) {
            throw new ImgValidException(ImgValidConstant.IMG_SCALE_MUST_LESS_THAN.formatted(maxWidth, maxHeight));
        }

        if (maxPixels > 0 && (long) width * height > maxPixels) {
            throw new ImgValidException(ImgValidConstant.IMG_PIXELS_OVER);
        }
        return new ArrayList<>(List.of(width, height));
    }

    /**
     * 校验图片格式是否合法。
     * 方法将从提供的图片信息对象中提取图片格式，并判断该格式是否在允许的格式集合中。
     *
     * @param name 图片的格式信息
     * @return 如果格式合法，返回图片的格式名称，类型为 {@code String}。
     * @throws ImgValidException 如果图片格式不在允许的格式集合中，则抛出此异常。
     */
    protected String validFormat(String name) throws ImgValidException {
        String format = name.toLowerCase();
        if (!allowedFormats.contains(format)) {
            throw new ImgValidException(ImgValidConstant.FORMAT_NOT_ALLOWED);
        }
        return format;
    }

    /**
     * 校验上传的图片文件是否合法，包括文件大小、媒体类型、图片格式、宽高尺寸等。
     * 如果所有校验均通过，则返回包含校验结果的 {@code Result} 对象；否则，抛出异常或返回失败结果。
     *
     * @param file 上传的图片文件，类型为 {@code MultipartFile}，不能为 {@code null} 或空。
     *             方法将校验文件的大小、媒体类型、图片格式及其宽高规模等是否符合要求。
     * @return 如果校验通过，返回一个包含校验结果的 {@code Result} 对象，包含文件校验信息、宽、高及其他元信息；
     * 如果校验失败，返回校验失败的 {@code Result} 对象或抛出异常。
     * @throws ImgValidException 如果文件大小、格式、媒体类型或图片尺寸不符，则抛出相应的校验异常。
     */
    public Result validate(MultipartFile file) throws ImgValidException {
        // 校验大小
        Long size = validSize(file);
        // 校验媒体类型
        validContentType(file);

        try (InputStream is = new BufferedInputStream(file.getInputStream())) {
            ImageInfo info = Imaging.getImageInfo(is, file.getOriginalFilename());
            // 校验图片格式
            String format = validFormat(info.getFormat()
                                            .getName());

            int width = info.getWidth();
            int height = info.getHeight();
            // 校验图片规模
            ArrayList<Integer> scale = validScale(width, height);

            return new Result(true, "校验通过", format, scale.removeFirst(), scale.removeFirst(), size);
        } catch (ImgValidException e) {
            throw e;
        } catch (Exception e) {
            return new Result(false, "解析图片失败: " + e.getMessage(), null, null, null, size);
        }
    }


    public record Result(
        boolean valid,
        String message,
        String format,
        Integer width,
        Integer height,
        Long size
    ) {}
}

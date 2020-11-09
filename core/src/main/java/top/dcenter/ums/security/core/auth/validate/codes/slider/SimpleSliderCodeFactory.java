/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.auth.validate.codes.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.validate.code.slider.SliderCodeFactory;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static top.dcenter.ums.security.core.auth.validate.codes.slider.SliderCodeUtil.cutImageByTemplate;
import static top.dcenter.ums.security.core.auth.validate.codes.slider.SliderCodeUtil.getImageByteBASE64;
import static top.dcenter.ums.security.core.auth.validate.codes.slider.SliderCodeUtil.getRandomImageFile;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.PATH_SEPARATOR;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.getAbsPath;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.getFileName;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.getImageAbsPath;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.getUuid;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.readFiles2CacheImageCodes;

/**
 * 简单的滑块验证码工厂, 自定义 {@link SliderCodeFactory}, 并注入 IOC 容器, 会替代此类
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/21 21:58
 */
@Slf4j
public class SimpleSliderCodeFactory implements SliderCodeFactory {

    /**
     * 缓存的验证码图片文件名称分隔符; 例如: x_y_w_h_token.temp
     */
    public static final String IMAGE_NAME_DELIMITER = "_";

    /**
     * 缓存的验证码图片文件名称根据 IMAGE_NAME_DELIMITER 切分 {@link String#split(String, int)} 方法中的 int 值
     */
    public static final Integer IMAGE_NAME_SPLIT_LIMIT = 5;

    /**
     * 缓存的滑块验证码大图的文件后缀名称; 例如: x_y_w_h_token.temp
     */
    public static final String SRC_SUFFIX = ".temp";
    /**
     * 缓存的滑块验证码小图的文件后缀名称; 例如: x_y_w_h_token.mark
     */
    public static final String MARK_SUFFIX = ".mark";

    /**
     * 缓存的存储的验证码图片数与实际需要的 totalImages 的百分比,
     * 如果低于此百分比, 重新生成图片缓存, 否则加载缓存中的图片验证码
     */
    private static final float PERCENTAGE = 0.9F;

    private final ValidateCodeProperties validateCodeProperties;

    /**
     * 抠图模板图片的绝对路径
     */
    private final String[] templateImagePaths;

    /**
     * 当支持缓存验证码时, 缓存的验证码图片的决定路径地址数组, 数组的大小为 totalImages
     */
    private final String[] originalImagePaths;

    /**
     * 当支持缓存验证码时, 缓存的验证码图片的决定路径地址数组, 数组的大小为 totalImages;<br>
     *     缓存的验证码图片文件名称格式: x_y_w_h_token.mark 或 x_y_w_h_token.temp
     */
    private volatile String[] codeImagePaths = null;

    /**
     * 缓存的验证码图片数
     */
    private final Integer totalImages;
    /**
     * 滑块验证码过期时间
     */
    private final Integer expireIn;
    /**
     * 在模板上抠图区灰阶等级: 4-10, 数值越高, 灰色越深
     */
    private final Integer grayscale;


    public SimpleSliderCodeFactory(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
        final ValidateCodeProperties.SliderCodeProperties slider = validateCodeProperties.getSlider();
        this.grayscale = slider.getGrayscale();
        this.totalImages = validateCodeProperties.getTotalImages();
        this.expireIn = slider.getExpire();
        this.templateImagePaths = getImagesAbsPaths(slider.getTemplateImageDirectory(), slider.getImageSuffix());
        this.originalImagePaths = getImagesAbsPaths(slider.getOriginalImageDirectory(), slider.getImageSuffix());
    }

    @PostConstruct
    public void init() {
        // 判断是否配置了图片验证码
        if (validateCodeProperties.getSlider().getAuthUrls().size() > 0) {
            // 从缓存中读取滑块验证码或者重新创建滑块验证码缓存
            readOrCreateCacheImageCodes();
        }
    }

    @Override
    public SliderCode getSliderCode() {

        try
        {
            final ThreadLocalRandom random = ThreadLocalRandom.current();
            if (codeImagePaths != null) {
                // 从缓存中获取验证码
                final String srcImageAbsPath = codeImagePaths[random.nextInt(totalImages)];
                final String markImageAbsPath = getMarkImageAbsPath(srcImageAbsPath);
                final byte[] srcImageBytes = Files.readAllBytes(Paths.get(srcImageAbsPath));
                final byte[] markImageBytes = Files.readAllBytes(Paths.get(markImageAbsPath));

                // 获取文件名称, 格式: x_y_w_h_token.temp
                final String srcImageFileName = getFileName(srcImageAbsPath);
                if (!StringUtils.hasText(srcImageFileName)) {
                    throw new ValidateCodeException(ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE, null, null);
                }

                return getSliderCode(srcImageBytes, markImageBytes, srcImageFileName);
            }

            // 实时生成逻辑
            final ImageInfo imageInfo = generateSliderImage(random, "");
            final byte[] srcImageBytes = getImageByteBASE64(imageInfo.sliderCodeInfo.srcImage);
            final byte[] markImageBytes = getImageByteBASE64(imageInfo.sliderCodeInfo.markImage);
            return getSliderCode(srcImageBytes,
                                 markImageBytes,
                                 imageInfo.srcImageAbsPath.substring(PATH_SEPARATOR.length()));

        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ValidateCodeException(ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE, null, null);
        }
    }

    @NonNull
    private SliderCode getSliderCode(byte[] srcImageBytes, byte[] markImageBytes, String srcImageFileName) {
        // 0 1 2 3 4
        // x_y_w_h_token.temp
        final String[] split = srcImageFileName.split(IMAGE_NAME_DELIMITER, IMAGE_NAME_SPLIT_LIMIT);
        if (split.length != IMAGE_NAME_SPLIT_LIMIT) {
            throw new ValidateCodeException(ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE, null, null);
        }

        // 获取 token
        String token = split[4];
        token = token.substring(0, token.lastIndexOf("."));

        int locationX = Integer.parseInt(split[0]);
        int locationY = Integer.parseInt(split[1]);
        int width = Integer.parseInt(split[2]);
        int height = Integer.parseInt(split[3]);

        return new SliderCode(null,
                              expireIn,
                              token,
                              new String(markImageBytes, StandardCharsets.UTF_8),
                              new String(srcImageBytes, StandardCharsets.UTF_8),
                              locationX,
                              locationY,
                              width,
                              height);
    }

    @Override
    public void refreshValidateCodeJob() {

        // 是否配置滑块验证码, 没有配置验证码直接忽视定时刷新任务
        if (this.validateCodeProperties.getSlider().getAuthUrls().size() < 1) {
            return;
        }

        final Instant now = Instant.now();
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        String[] oldCodeImagePaths = this.codeImagePaths;
        final String[] newImageCodePaths = new String[totalImages];
        ValidateCodeProperties.SliderCodeProperties slider = validateCodeProperties.getSlider();
        final String codeImageAbsPath = getAbsPath(slider.getCodeImageDirectory());

        // 1. 创建新验证码图片
        // 存储 newImageCodePaths 数组索引位置
        List<Integer> failures = new CopyOnWriteArrayList<>();
        // 存储异步执行 future
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int y = 0; y < totalImages; y++) {
            try {
                // 生成滑块验证码图片
                final ImageInfo imageInfo = generateSliderImage(random, codeImageAbsPath);
                newImageCodePaths[y] = imageInfo.srcImageAbsPath;
                // 创建异步执行图片验证码生成 future
                final int index = y;
                final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                    try {
                        // 写入文件
                        write2File(imageInfo.srcImageAbsPath, imageInfo.sliderCodeInfo.srcImage);
                        write2File(imageInfo.markImageAbsPath, imageInfo.sliderCodeInfo.markImage);
                    }
                    catch (Exception e) {
                        // 创建图片验证码失败, 添加 newImageCodePaths 索引信息
                        failures.add(index);
                    }
                });
                futures.add(voidCompletableFuture);
            }
            catch (Exception e) {
                log.error("创建新滑块验证码图片失败: " + e.getMessage(), e);
            }
        }
        try {
            // 等待异步执行完成,
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                ThreadLocalRandom localRandom = ThreadLocalRandom.current();
                // 对创建图片验证码失败的结果进行重新创建, 如果再此创建不成功, 随机赋值一个已成功的图片验证码路径.
                for (Integer index : failures) {
                    try {
                        // 生成滑块验证码图片
                        final ImageInfo imageInfo = generateSliderImage(localRandom, codeImageAbsPath);
                        newImageCodePaths[index] = imageInfo.srcImageAbsPath;
                        // 写入文件
                        write2File(imageInfo.srcImageAbsPath, imageInfo.sliderCodeInfo.srcImage);
                        write2File(imageInfo.markImageAbsPath, imageInfo.sliderCodeInfo.markImage);
                    }
                    catch (Exception e) {
                        // 随机获取一个已有的缓存验证码赋值
                        newImageCodePaths[index] = getImageAbsPath(newImageCodePaths);
                    }
                }
            }).get();
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("创建新滑块验证码图片失败: " + e.getMessage(), e);
        }

        this.codeImagePaths = newImageCodePaths;

        // 2. 删除旧的验证码图片, 删除失败的文件都记录在日志文件中, 方便后续清除.
        if (oldCodeImagePaths != null) {
            for (int i = 0; i < totalImages; i++) {
                final String srcImageCodePath = oldCodeImagePaths[i];
                try {
                    boolean deleteIfExists = Files.deleteIfExists(Paths.get(srcImageCodePath));
                    if (!deleteIfExists) {
                        log.warn("删除旧的验证码图片失败: {}", srcImageCodePath);
                    }
                    String markImageCodePath = getMarkImageAbsPath(srcImageCodePath);
                    deleteIfExists = Files.deleteIfExists(Paths.get(markImageCodePath));
                    if (!deleteIfExists) {
                        log.warn("删除旧的验证码图片失败: {}", markImageCodePath);
                    }
                }
                catch (Exception e) {
                    log.warn("删除旧的验证码图片失败: " + srcImageCodePath, e);
                }
            }
            // 设置为 null, 加速垃圾回收
            //noinspection UnusedAssignment
            oldCodeImagePaths = null;
        }
        log.info("生成图片验证码任务总耗时={} 毫秒", Instant.now().toEpochMilli() - now.toEpochMilli());
    }

    private void readOrCreateCacheImageCodes() {
        final Instant now = Instant.now();

        String[] newTempImagePaths = new String[totalImages];
        final ValidateCodeProperties.SliderCodeProperties slider = validateCodeProperties.getSlider();
        final String codeImageAbsPath = getAbsPath(slider.getCodeImageDirectory());
        final File fileDirs = Paths.get(codeImageAbsPath).toFile();

        if (fileDirs.isDirectory() && fileDirs.canRead()) {
            final List<File> fileList = Arrays.stream(Optional.ofNullable(fileDirs.listFiles()).orElse(new File[0]))
                                              .filter(File::isFile)
                                              .filter(f -> f.getName().endsWith(SRC_SUFFIX))
                                              .collect(Collectors.toList());

            // 获取图片验证码缓存
            final int size = fileList.size();
            if (size >= (int) (totalImages * PERCENTAGE)) {
                readFiles2CacheImageCodes(newTempImagePaths, fileList);
                this.codeImagePaths = newTempImagePaths;
            }
            // 删除缓存, 重新创建图片验证码缓存
            else {
                fileList.forEach(f -> {
                    try {
                        final Path srcFilePath = f.toPath();
                        Files.delete(srcFilePath);
                        final String fileAbsolutePath = f.getAbsolutePath();
                        String markAbsPath = getMarkImageAbsPath(fileAbsolutePath);
                        Files.delete(Paths.get(markAbsPath));
                    }
                    catch (IOException e) {
                        String msg = String.format("删除缓存的滑块验证码 %s 错误: %s", f.getPath(), e.getMessage());
                        log.error(msg, e);
                    }
                });
                refreshValidateCodeJob();
            }

        }

        log.info("从缓存中读取或创建滑块验证码总耗时: {} 毫秒", Instant.now().toEpochMilli() - now.toEpochMilli());

    }

    /**
     * 根据抠图后的源图片绝对路径, 获取抠图图片的绝对路径
     * @param srcImageAbsPath   抠图后的源图片绝对路径
     * @return                  抠图图片的绝对路径
     */
    @NonNull
    private String getMarkImageAbsPath(@NonNull String srcImageAbsPath) {
        return srcImageAbsPath.substring(0, srcImageAbsPath.length() - SRC_SUFFIX.length()).concat(MARK_SUFFIX);
    }

    /**
     * 获取指定目录下的指定的后缀的图片数组
     * @param imageDirectory    存储图片的目录
     * @param imageSuffix       图片后缀
     * @return  指定后缀的图片绝对路径的数组
     */
    private String[] getImagesAbsPaths(String imageDirectory, String imageSuffix) {
        final String originalAbsPath = ValidateCodeUtil.getAbsPath(imageDirectory);
        final File file = Paths.get(originalAbsPath).toFile();
        if (file.exists()) {
            final List<String> filePathList = Arrays.stream(Optional.ofNullable(file.listFiles()).orElse(new File[0]))
                                                    .filter(File::isFile)
                                                    .map(File::getAbsolutePath)
                                                    .filter(path -> path.endsWith(imageSuffix))
                                                    .collect(Collectors.toList());
            if (filePathList.size() > 0) {
                return filePathList.toArray(new String[0]);
            }
        }
        throw new RuntimeException(String.format("在目录 %s 中没有滑块验证码图片(%s), 请确保至少一张图片", imageDirectory, imageSuffix));
    }

    /**
     * 把 image 用 Base64 编码后写入 absPath 文件中
     * @param absPath   图片绝对路径
     * @param image     {@link BufferedImage}
     * @throws IOException  Base64 编码发生异常
     */
    private void write2File(String absPath, BufferedImage image) throws IOException {
        Files.write(Paths.get(absPath),
                    getImageByteBASE64(image),
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE);
    }

    /**
     * 生成滑块验证码图片
     * @param random            {@link ThreadLocalRandom}
     * @param codeImageAbsPath  生成的滑块验证码图片存放目录
     * @return                  生成的滑块验证码图片信息
     * @throws IOException  生成滑块验证码错误
     */
    private ImageInfo generateSliderImage(ThreadLocalRandom random, String codeImageAbsPath) throws IOException {
        final File templateImage = getRandomImageFile(this.templateImagePaths, random);
        final File originalImage = getRandomImageFile(this.originalImagePaths, random);
        final SliderCodeInfo sliderCodeInfo = createSliderCodeInfo(templateImage, originalImage, this.grayscale);

        // 不包含后缀信息的文件名称
        // x_y_w_h_token
        final String fileName = sliderCodeInfo.fileName.toString();
        // 0 1 2 3 4
        // x_y_w_h_token.temp
        String srcImageFileName = fileName + SRC_SUFFIX;
        // x_y_w_h_token.mark
        String markImageFileName = fileName + MARK_SUFFIX;
        final String srcImageAbsPath = codeImageAbsPath + PATH_SEPARATOR + srcImageFileName;
        final String markImageAbsPath = codeImageAbsPath + PATH_SEPARATOR + markImageFileName;

        return new ImageInfo(srcImageAbsPath, markImageAbsPath, sliderCodeInfo);
    }

    /**
     * 创建滑块验证码图片
     * @param templateFile          模板图片文件
     * @param originalFile          源图片文件
     * @param grayscale             在模板上抠图区灰阶等级: 4-10, 数值越高, 灰色越深
     * @return                      生成的滑块验证码信息
     */
    private SliderCodeInfo createSliderCodeInfo(File templateFile, File originalFile, int grayscale) throws IOException {
        BufferedImage templateImage = ImageIO.read(templateFile);
        BufferedImage oriImage = ImageIO.read(originalFile);
        final Map<String, Object> sliderCodeInfoMap = cutImageByTemplate(templateImage, oriImage, grayscale);

        BufferedImage markImage = (BufferedImage) sliderCodeInfoMap.get("markImage");
        BufferedImage srcImage = (BufferedImage) sliderCodeInfoMap.get("srcImage");

        // 构建缓存的文件名称
        final StringBuilder fileName = new StringBuilder();
        fileName.append(sliderCodeInfoMap.get("locationX"))
                .append(IMAGE_NAME_DELIMITER)
                .append(sliderCodeInfoMap.get("locationY"))
                .append(IMAGE_NAME_DELIMITER)
                .append(srcImage.getWidth())
                .append(IMAGE_NAME_DELIMITER)
                .append(srcImage.getHeight())
                .append(IMAGE_NAME_DELIMITER)
                .append(getUuid());

        return new SliderCodeInfo(srcImage, markImage, fileName);

    }

    /**
     * 为了提取重复代码, 重构方法而设置的内部类, 主要目的为传递多个变量值
     */
    private static class SliderCodeInfo {

        /**
         * 抠图后的源图片
         */
        private final BufferedImage srcImage;
        /**
         * 抠图图片
         */
        private final BufferedImage markImage;
        /**
         * 不带后缀的文件名称: x_y_w_h_token
         */
        private final StringBuilder fileName;

        public SliderCodeInfo(BufferedImage srcImage, BufferedImage markImage, StringBuilder fileName) {
            this.srcImage = srcImage;
            this.markImage = markImage;
            this.fileName = fileName;
        }

    }

    /**
     * 为了提取重复代码, 重构方法而设置的内部类, 主要目的为传递多个变量值
     */
    private static class ImageInfo {

        private final String srcImageAbsPath;
        private final String markImageAbsPath;
        private final SliderCodeInfo sliderCodeInfo;

        ImageInfo(String srcImageAbsPath, String markImageAbsPath, SliderCodeInfo sliderCodeInfo) {
            this.srcImageAbsPath = srcImageAbsPath;
            this.markImageAbsPath = markImageAbsPath;
            this.sliderCodeInfo = sliderCodeInfo;
        }
    }

}
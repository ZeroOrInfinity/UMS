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

package top.dcenter.ums.security.core.auth.validate.codes.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.core.api.validate.code.image.ImageCodeFactory;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static top.dcenter.ums.security.common.utils.UuidUtils.getUUID;
import static top.dcenter.ums.security.core.auth.validate.codes.image.ImageUtil.IMAGE_TYPE;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.PATH_SEPARATOR;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.generateVerifyCode;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.getAbsPath;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.getFileName;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.getImageAbsPath;
import static top.dcenter.ums.security.core.util.ValidateCodeUtil.readFiles2CacheImageCodes;

/**
 * 图片验证码工厂, 默认实现, 自定义 {@link ImageCodeFactory}, 并注入 IOC 容器, 会替代此类
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/22 11:23
 */
@Slf4j
public class DefaultImageCodeFactory implements ImageCodeFactory {

    /**
     * 缓存的验证码图片文件名称分隔符; 例如: validateCode_uuid.png
     */
    public static final String IMAGE_NAME_DELIMITER = "_";

    /**
     * 缓存的存储的验证码图片数与实际需要的 totalImages 的百分比,
     * 如果低于此百分比, 重新生成图片缓存, 否则加载缓存中的图片验证码
     */
    private static final float PERCENTAGE = 0.9F;

    private final ValidateCodeProperties validateCodeProperties;

    /**
     * 当支持缓存验证码时, 缓存的验证码图片的决定路径地址数组, 数组的大小为 totalImages;<br>
     *     缓存的验证码图片文件名称格式: validateCode_uuid.png
     */
    private volatile String[] imageCodePaths = null;

    /**
     * 缓存的验证码图片数
     */
    private final Integer totalImages;

    public DefaultImageCodeFactory(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
        this.totalImages = validateCodeProperties.getTotalImages();
    }

    @PostConstruct
    public void init() {
        // 判断是否配置了图片验证码
        if (this.validateCodeProperties.getImage().getAuthUrls().size() > 0) {
            // 从缓存中读取滑块验证码或者重新创建滑块验证码缓存
            readOrCreateCacheImageCodes();
        }
    }

    @Override
    public ImageCode getImageCode(ServletRequest request) {

        ValidateCodeProperties.ImageCodeProperties imageProp = this.validateCodeProperties.getImage();
        int expireIn = imageProp.getExpire();
        final Integer codeLength = imageProp.getLength();

        // 从缓存中获取验证码
        if (imageCodePaths != null) {

            final String imageCodePath = getImageAbsPath(imageCodePaths);

            final String imageFileName = getFileName(imageCodePath);

            if (StringUtils.hasText(imageFileName)) {
                final String code = imageFileName.substring(0, codeLength);
                return new ImageCode(imageCodePath, code, expireIn);
            }

        }

        // 后备: 实时生成验证码
        int width = imageProp.getWidth();
        int height = imageProp.getHeight();
        String code = generateVerifyCode(codeLength);
        String imageFileName = code + IMAGE_NAME_DELIMITER + getUUID() + "." + IMAGE_TYPE;
        final String imageAbsPath = getAbsPath(imageProp.getImageCacheDirectory()) + PATH_SEPARATOR + imageFileName;

        // 创建图片验证码并生成图片文件
        try (final OutputStream outputStream =
                     Files.newOutputStream(Paths.get(imageAbsPath), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            ImageUtil.outputImage(width, height, outputStream, code);
            return new ImageCode(imageAbsPath, code, expireIn);
        }
        catch (Exception e) {
            // 创建图片验证码失败
            log.warn(String.format("生成验证码图片 %s 失败: %s", imageAbsPath, e.getMessage()), e);
            return null;
        }

    }

    /**
     * 应用启动时读取缓存的图片验证码, 当缓存的图片验证码不存在或数量不符合时, 删除缓存的验证码并重新创建缓存图片验证码.
     */
    private void readOrCreateCacheImageCodes() {

        final Instant now = Instant.now();

        final String[] newImageCodePaths = new String[totalImages];
        final ValidateCodeProperties.ImageCodeProperties image = validateCodeProperties.getImage();
        final String imageAbsPath = getAbsPath(image.getImageCacheDirectory());
        final File file = Paths.get(imageAbsPath).toFile();

        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("创建图片验证码图片缓存目录失败!");
        }

        if (file.isDirectory() && file.canRead()) {
            final List<File> fileList = Arrays.stream(Optional.ofNullable(file.listFiles()).orElse(new File[0]))
                                              .filter(File::isFile)
                                              .collect(Collectors.toList());
            // 获取图片验证码缓存
            final int size = fileList.size();
            if (size >= (int) (totalImages * PERCENTAGE)) {
                readFiles2CacheImageCodes(newImageCodePaths, fileList);
                this.imageCodePaths = newImageCodePaths;
            }
            // 删除缓存, 重新创建图片验证码缓存
            else {
                fileList.forEach(f -> {
                    try {
                        Files.delete(f.toPath());
                    }
                    catch (IOException e) {
                        String msg = String.format("删除缓存的图片验证码 %s 错误: %s", f.getPath(), e.getMessage());
                        log.error(msg, e);
                    }
                });
                refreshValidateCodeJob();
            }
        }

        log.info("从缓存中读取或创建图片验证码总耗时: {} 毫秒", Instant.now().toEpochMilli() - now.toEpochMilli());

    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"REC_CATCH_EXCEPTION"})
    @SuppressWarnings("ConstantConditions")
    @Override
    public void refreshValidateCodeJob() {

        // 没有配置图片验证码直接返回
        if (this.validateCodeProperties.getImage().getAuthUrls().size() < 1) {
            return;
        }

        final Instant now = Instant.now();
        String[] oldImageCodePaths = this.imageCodePaths;
        final String[] newImageCodePaths = new String[totalImages];

        final ValidateCodeProperties.ImageCodeProperties image = validateCodeProperties.getImage();
        final Integer imageCodeLength = image.getLength();
        final Integer height = image.getHeight();
        final Integer width = image.getWidth();

        // 1. 创建新验证码图片
        // 存储创建失败的图片验证码路径, 此路径包含 newImageCodePaths 数组索引位置, 例如: index_absImagePath
        List<String> failures = new CopyOnWriteArrayList<>();
        // 存储异步执行 future
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int y = 0; y < totalImages; y++) {
            final String code = generateVerifyCode(imageCodeLength);
            String imageFileName = code + IMAGE_NAME_DELIMITER + getUUID() + "." + IMAGE_TYPE;
            final String imageAbsPath = getAbsPath(image.getImageCacheDirectory()) + PATH_SEPARATOR + imageFileName;
            newImageCodePaths[y] = imageAbsPath;

            // 创建异步执行图片验证码生成 future
            final int index = y;
            final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                try (final OutputStream outputStream =
                             Files.newOutputStream(Paths.get(imageAbsPath), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    ImageUtil.outputImage(width, height, outputStream, code);
                }
                catch (Exception e) {
                    // 创建图片验证码失败, 添加 newImageCodePaths 索引信息添加到图片验证码路径上, 例如: index_absImagePath
                    failures.add(index + IMAGE_NAME_DELIMITER + imageAbsPath);
                }
            });
            futures.add(voidCompletableFuture);
        }

        try {
            // 等待异步执行完成,
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                // 并对创建图片验证码失败的结果进行重新创建, 如果再此创建不成功, 随机赋值一个已成功的图片验证码路径.
                for (String failure : failures) {
                    final String[] split = failure.split(IMAGE_NAME_DELIMITER, 2);
                    final String imageAbsPath = split[1];
                    try (final OutputStream outputStream =
                                 Files.newOutputStream(Paths.get(imageAbsPath), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                        final String fileName = imageAbsPath.substring(imageAbsPath.indexOf(PATH_SEPARATOR) + 1);
                        final String code = fileName.substring(0, fileName.indexOf(IMAGE_NAME_DELIMITER));
                        ImageUtil.outputImage(width, height, outputStream, code);
                    }
                    catch (Exception e) {
                        newImageCodePaths[Integer.parseInt(split[0])] = getImageAbsPath(newImageCodePaths);
                    }
                }
            }).get();

        }
        catch (InterruptedException | ExecutionException e) {
            log.error("创建新验证码图片失败: " + e.getMessage(), e);
        }

        this.imageCodePaths = newImageCodePaths;

        // 2. 删除旧的验证码图片, 删除失败的文件都记录在日志文件中, 方便后续清除.
        if (oldImageCodePaths != null) {
            //noinspection ConstantConditions
            for (int i = 0; i < totalImages; i++) {
                try {
                    final boolean deleteIfExists = Files.deleteIfExists(Paths.get(oldImageCodePaths[i]));
                    if (!deleteIfExists) {
                        log.warn("删除旧的验证码图片失败: {}", oldImageCodePaths[i]);
                    }
                }
                catch (Exception e) {
                    log.warn("删除旧的验证码图片失败: " + oldImageCodePaths[i], e);
                }
            }
            // 设置为 null, 有助于垃圾回收
            //noinspection UnusedAssignment
            oldImageCodePaths = null;
        }

        log.info("生成图片验证码任务总耗时={} 毫秒", Instant.now().toEpochMilli() - now.toEpochMilli());
    }

}
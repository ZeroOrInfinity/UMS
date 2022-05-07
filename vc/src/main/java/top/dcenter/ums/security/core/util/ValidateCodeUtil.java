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

package top.dcenter.ums.security.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码工具
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/4 9:25
 */
@Slf4j
public final class ValidateCodeUtil {

    private ValidateCodeUtil() { }

    private final static byte[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

    /**
     * 路径分隔符
     */
    public static final String PATH_SEPARATOR = "/";

    /**
     * 类路径前缀
     */
    public static final String CLASS_PATH = "classpath:";
    /**
     * ENV 中系统 key
     */
    public static final String OS_KEY = "OS";
    /**
     * windows
     */
    public static final String WINDOWS = "Windows";


    /**
     * 获取 imageDirectory 绝对路径
     * @param imageDirectory    图片路径, 不以 "classpath:" 开头时即认为是绝对路径, 以 "classpath:" 开头时即认为是基于 classpath 的相对路径.
     * @return  随机图片的决定路径
     */
    public static String getAbsPath(@NonNull String imageDirectory) {

        try
        {
            File dirFile = ResourceUtils.getFile(imageDirectory);
            return dirFile.getAbsolutePath();
        }
        catch (IOException e)
        {
            try {
                // 创建目录
                if (imageDirectory.startsWith(CLASS_PATH)) {
                    final URL absClasspathUrl = ResourceUtils.getURL(CLASS_PATH);
                    String absClasspath = absClasspathUrl.getPath();
                    if (System.getenv(OS_KEY) != null && System.getenv(OS_KEY).startsWith(WINDOWS)) {
                        absClasspath = absClasspath.substring(1);
                    }
                    String correctImageDirectory = imageDirectory.substring(CLASS_PATH.length());
                    Files.createDirectories(Paths.get(absClasspath, correctImageDirectory));
                }
                else {
                    Files.createDirectories(Paths.get(imageDirectory));
                }

                return getAbsPath(imageDirectory);
            }
            catch (IOException ex) {
                String msg = String.format("获取 %s 绝对路径失败: %s", imageDirectory, e.getMessage());
                throw new RuntimeException(msg, ex);
            }
        }
    }

    /**
     * 根据绝对逻辑路径获取文件名称
     * @param absPath   文件的绝对路径
     * @return  返回文件名称, 如果 absPath 格式错误(路径分隔符不是 / 或 \)返回 null
     */
    public static String getFileName(@NonNull String absPath) {
        int lastIndexOf = absPath.lastIndexOf(PATH_SEPARATOR);

        // 兼容 windows 系统, 方便测试
        if (lastIndexOf == -1) {
            lastIndexOf = absPath.lastIndexOf(File.separator);
        }

        if (lastIndexOf == -1) {
            return null;
        }

        return absPath.substring(lastIndexOf + 1);
    }

    /**
     * 从 fileList 中获取绝对路径存储到 newTemplateImagePaths 数组中
     * @param newImagePaths 存储验证码文件的绝对路径
     * @param fileList      验证码文件的 File 列表
     */
    public static void readFiles2CacheImageCodes(String[] newImagePaths, List<File> fileList) {
        int size = fileList.size();
        int totalImages = newImagePaths.length;
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < totalImages; i++) {
            if (i < size) {
                newImagePaths[i] = fileList.get(i).getAbsolutePath();
            }
            else {
                newImagePaths[i] = fileList.get(random.nextInt(size)).getAbsolutePath();
            }
        }
    }

    /**
     * 从 imageCodePaths 中随机获取一个验证码的绝对路径
     * @param imageCodePaths    验证码的绝对路径数组
     * @return  验证码的绝对路径
     */
    public static String getImageAbsPath(@NonNull String[] imageCodePaths) {
        int index = ThreadLocalRandom.current().nextInt(imageCodePaths.length);
        final String imageCodePath = imageCodePaths[index];
        if (StringUtils.hasText(imageCodePath)) {
            return imageCodePath;
        }
        return getImageAbsPath(imageCodePaths);
    }

    /**
     * 从 imageDirectory 路径下获取随机图片绝对路径
     * @param imageDirectory    图片路径, 不以 "classpath:" 开头时即认为是绝对路径, 以 "classpath:" 开头时即认为是基于 classpath 的相对路径.
     * @return  随机图片的决定路径
     */
    public static String getRandomImageAbsPath(@NonNull String imageDirectory) {

        File dirFile;
        try
        {
            dirFile = ResourceUtils.getFile(imageDirectory);
            String[] fileNames;
            if (dirFile.isDirectory())
            {
                fileNames = dirFile.list();
                if (fileNames != null)
                {
                    int r = ThreadLocalRandom.current().nextInt(fileNames.length);
                    return String.format("%s%s%s", dirFile.getPath(), PATH_SEPARATOR, fileNames[r]);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            log.info(String.format( "生成验证码图片时找不到图片源, 图片源路径: %s", imageDirectory), e);
        }

        log.info("生成验证码图片时找不到图片源, 图片源路径: {}", imageDirectory);
        throw new RuntimeException("找不到图片源, 图片源路径: " + imageDirectory);

    }

    /**
     * 使用默认字符源(2-9a-zA-Z)生成验证码
     *
     * @param verifySize 验证码长度, not null
     * @return  验证码字符串
     */
    public static String generateVerifyCode(@NonNull Integer verifySize) {
        return generateVerifyCode(verifySize, false);
    }

    /**
     * 使用数字(0-9)生成验证码
     *
     * @param verifySize 验证码长度, not null
     * @return  验证码字符串
     */
    public static String generateNumberVerifyCode(@NonNull Integer verifySize) {
        return generateVerifyCode(verifySize, true);
    }

    /**
     * 使用指定源生成验证码
     *
     * @param verifySize    验证码长度
     * @param isNumber      是否数字验证码, 用于非数字验证码时去除 0 和 1 两个数字
     * @return  验证码字符串
     */
    private static String generateVerifyCode(int verifySize, boolean isNumber) {
        Random rand = ThreadLocalRandom.current();
        int length = DIGITS.length;
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++)
        {
            if (isNumber)
            {
                // 纯数字
                verifyCode.append((char) DIGITS[rand.nextInt(10)]);
            }
            else
            {
                // 去除 0 和 1 两个数字
                verifyCode.append((char) DIGITS[Math.abs(rand.nextInt(length - 2) + 2)]);
            }
        }
        return verifyCode.toString();
    }


}
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
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 滑块验证工具类<br>
 *
 * @author : YongWu zheng
 * @version : V1.0  Created by 2020/9/21 12:33
 */
@Slf4j
public class SliderCodeUtil {

    /**
     * 随机获取一张图片对象
     *
     * @param imageAbsPaths 图片的绝对路径的数组
     * @return File             图片文件
     */
    public static File getRandomImageFile(String[] imageAbsPaths, ThreadLocalRandom random) {
        final String imageAbsPath = imageAbsPaths[random.nextInt(imageAbsPaths.length)];
        return Paths.get(imageAbsPath).toFile();
    }

    /**
     * 将图片转换为BASE64
     *
     * @param image image
     * @return 图片字节数组
     * @throws IOException IOException
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static byte[] getImageByteBASE64(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        // 转成 byte 数组
        byte[] bytes = out.toByteArray();
        return Base64Utils.encode(bytes);
    }

    /**
     * 源图片宽必须大于模板图片宽的倍数
     */
    public static final int WIDTH_RATE = 3;
    /**
     * 源图片高必须大于模板图片高的倍数
     */
    public static final int HEIGHT_RATE = 2;

    /**
     * 根据模板抠图
     *
     * @param imageTemplate 模板图片
     * @param imageTarget   源图片
     * @param grayscale     在模板上抠图区灰阶等级: 4-10, 数值越高, 灰色越深
     * @return 返回生成的滑块图片数据: 处理后的源图片(srcImage)与处理后模板图片(markImage), 以及 X 轴(locationX), Y 轴(locationY)信息.
     */
    public static Map<String, Object> cutImageByTemplate(BufferedImage imageTemplate, BufferedImage imageTarget,
                                                         int grayscale) {

        Map<String, Object> sliderCodeInfoMap = new HashMap<>(4);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 模板图宽高
        int width = imageTemplate.getWidth();
        int height = imageTemplate.getHeight();

        // 源图宽高
        int srcWidth = imageTarget.getWidth();
        int srcHeight = imageTarget.getHeight();

        // 检查模板图片宽高是否符合源图片的宽高
        if (srcWidth / width < WIDTH_RATE && srcHeight / height < HEIGHT_RATE) {
            String msg = String.format("模板图片宽高不符合源图片的宽高: 源图片宽必须大于模板图片宽的 %d 倍, 源图片高必须大于模板图片高的 %d 倍",
                                       WIDTH_RATE, HEIGHT_RATE);
            throw new RuntimeException(msg);
        }

        // 随机生成抠图位置
        final int offsetX = width / 10;
        int locationX = offsetX + random.nextInt(srcWidth - width - offsetX);
        final int offsetY = height / 10;
        int locationY = offsetY + random.nextInt(srcHeight - height - offsetY);

        // 抠图后对应的模板图片
        Graphics2D graphics = imageTemplate.createGraphics();
        graphics.setBackground(Color.white);

        // 对源图片进行抠图, 把抠除的图片放置到对应的模板图片 imageTemplate 上
        cutImageByTemplate(imageTarget, imageTemplate, grayscale, locationX, locationY);

        // 设置“抗锯齿”的属性
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        graphics.drawImage(imageTemplate, 0, 0, null);
        graphics.dispose();

        sliderCodeInfoMap.put("markImage", imageTemplate);
        sliderCodeInfoMap.put("srcImage", imageTarget);
        sliderCodeInfoMap.put("locationX", locationX);
        sliderCodeInfoMap.put("locationY", locationY);

        return sliderCodeInfoMap;
    }

    /**
     * 对源图片进行抠图, 并生成对应模板图片.
     *
     * @param oriImage      源图片
     * @param templateImage 模板图片
     * @param grayscale     在模板上抠图区灰阶等级: 4-10, 数值越高, 灰色越深
     * @param locationX     X 轴
     * @param locationY     Y 轴
     */
    private static void cutImageByTemplate(BufferedImage oriImage, BufferedImage templateImage,
                                           int grayscale,
                                           int locationX, int locationY) {
        // 源文件图像矩阵
        int[][] oriImageData = getImageData(oriImage);
        // 模板图像矩阵
        int[][] templateImageData = getImageData(templateImage);
        // 源图片高宽
        final int width = oriImageData.length;
        final int height = oriImageData[0].length;
        // 模板图片高宽
        final int cutWidth = templateImageData.length;
        final int cutHeight = templateImageData[0].length;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // 是否模板区域
                final boolean isMarkTarget = (i >= locationX && j >= locationY) && (i < locationX + cutWidth && j < locationY + cutHeight);
                if (isMarkTarget) {

                    int rgb = oriImage.getRGB(i, j);
                    int templateRgb = templateImage.getRGB(i - locationX, j - locationY);
                    //noinspection ConditionCoveredByFurtherCondition
                    if (templateRgb != 16777215 && templateRgb != -1 && templateRgb <= 0) {
                        // 抠图上复制对应颜色值
                        templateImage.setRGB(i - locationX, j - locationY, rgb);
                        // 对源图片的抠图区域进行灰度处理
                        int r = (0xff & rgb);
                        int g = (0xff & (rgb >> 8));
                        int b = (0xff & (rgb >> 16));
                        int gray = (r + g + b) / grayscale;
                        rgb = 255 << 24 | gray << 16 | gray << 8 | gray;
                        oriImage.setRGB(i, j, rgb);
                    }
                    else {
                        rgb = rgb & 0x00ffffff;
                        templateImage.setRGB(i - locationX, j - locationY, rgb);
                    }

                }
            }
        }

    }

    /**
     * 生成图像矩阵
     *
     * @param bufferedImage {@link BufferedImage}
     * @return 字节流图片
     */
    private static int[][] getImageData(BufferedImage bufferedImage) {
        int[][] data = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                data[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return data;
    }

}
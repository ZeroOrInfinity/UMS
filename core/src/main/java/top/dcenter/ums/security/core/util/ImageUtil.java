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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/4 9:25
 */
@SuppressWarnings({"AlibabaUndefineMagicConstant", "unused"})
public class ImageUtil {

    /**
     * 生成随机验证码文件,并返回验证码值
     *
     * @param w     图片宽度
     * @param h     图片高度
     * @param outputFile    输出文件
     * @param verifySize    验证码长度
     * @return  验证码
     * @throws IOException  IOException
     */
    public static String outputVerifyImage(int w, int h, File outputFile, int verifySize) throws IOException {
        String verifyCode = ValidateCodeUtil.generateVerifyCode(verifySize);
        outputImage(w, h, outputFile, verifyCode);
        return verifyCode;
    }

    /**
     * 输出随机验证码图片流,并返回验证码值
     *
     * @param w     图片宽度
     * @param h     图片高度
     * @param os    输出流
     * @param verifySize    验证码长度
     * @return  验证码
     * @throws IOException IOException
     */
    public static String outputVerifyImage(int w, int h, OutputStream os, int verifySize) throws IOException {
        String verifyCode = ValidateCodeUtil.generateVerifyCode(verifySize);
        outputImage(w, h, os, verifyCode);
        return verifyCode;
    }

    /**
     * 生成指定验证码图像文件
     *
     * @param w     图片宽度
     * @param h     图片高度
     * @param outputFile    输出文件
     * @param code  验证码
     * @throws IOException  IOException
     */
    public static void outputImage(int w, int h, File outputFile, String code) throws IOException {
        if (outputFile == null) {
            return;
        }
        File dir = outputFile.getParentFile();
        try {
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            boolean newFile = outputFile.createNewFile();
            if (!newFile)
            {
                throw new IOException("输出图片失败");
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            outputImage(w, h, fos, code);
            fos.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 输出指定验证码图片流
     *
     * @param w     图片宽度
     * @param h     图片高度
     * @param os    输出流
     * @param code  验证码
     * @throws IOException  IOException
     */
    public static void outputImage(int w, int h, OutputStream os, String code) throws IOException {
        BufferedImage image = getBufferedImage(w, h, code);
        ImageIO.write(image, "jpg", os);
    }

    /**
     * 根据提供的 code 产生 BufferedImage
     * @param width     图片宽度
     * @param height     图片高度
     * @param code  验证码
     * @return  BufferedImage
     */
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    public static BufferedImage getBufferedImage(int width, int height, String code) {
        int verifySize = code.length();
        int tempWidth = height * 45 / 10;
        width = Math.max(width, tempWidth);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[] { Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA,
                Color.ORANGE, Color.PINK, Color.YELLOW };
        float[] fractions = new float[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);

        // 设置边框色
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, width, height);

        Random random = new Random();

        Color c = getRandColor(200, 250, random);
        // 设置背景色
        g2.setColor(c);
        g2.fillRect(0, 2, width, height - 4);

        // 绘制干扰线
        // 设置线条的颜色
        g2.setColor(getRandColor(160, 200, random));
        int lineLength = 20;
        for (int i = 0; i < lineLength; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        // 添加噪点
        // 噪声率
        float yawpRate = 0.05f;
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = getRandomIntColor(random);
            image.setRGB(x, y, rgb);
        }

        // 使图片扭曲
        shear(g2, width, height, c, random);

        g2.setColor(getRandColor(100, 160, random));
        int yPending = (int) (height * 0.05F);
        int fontSize = height - yPending;
        Font font = new Font("Arial", Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = code.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            int xPending = (int) (width * 0.1F);
            int dynamicPending = ((width - xPending) / verifySize) * i;
            if (i == 0) {
                dynamicPending = dynamicPending + xPending;
            }
            else {
                dynamicPending = dynamicPending + xPending / 2;
            }
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1),
                                 (double) dynamicPending + fontSize / ((double) 2),
                                 (height - yPending) / ((double) 2));
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, dynamicPending,
                         height / 2 + fontSize / 2 - yPending);
        }

        g2.dispose();
        return image;
    }

    private static Color getRandColor(int fc, int bc, Random random) {
        if (fc > 255)
        {
            fc = 255;
        }
        if (bc > 255)
        {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor(Random random) {
        int[] rgb = getRandomRgb(random);
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb(Random random) {
        int[] rgb = new int[3];
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color, Random random) {
        shearX(g, w1, h1, color, true, random);
        shearY(g, w1, h1, color, true, random);
    }

    @SuppressWarnings({"SameParameterValue", "ConstantConditions"})
    private static void shearX(Graphics g, int w1, int h1, Color color, boolean borderGap, Random random) {

        int period = random.nextInt(2);

        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap)
            {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    @SuppressWarnings("SameParameterValue")
    private static void shearY(Graphics g, int w1, int h1, Color color, boolean borderGap, Random random) {

        // 50;
        int period = random.nextInt(40) + 10;

        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap)
            {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }
}
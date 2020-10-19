package top.dcenter.ums.security.core.auth.validate.codes.slider;

import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 滑块验证工具类<br>
 *
 * @author : LeonardozzZ223 https://blog.csdn.net/adidas74891496/article/details/106281363
 * @version : Created in 10:57 2018/6/25
 */
public class SliderCodeUtil {

    /**
     * 根据传入的路径生成指定验证码图片
     *
     * @param filePath              文件路径
     * @param cutWidth              模板图宽度
     * @param cutHeight             模板图高度
     * @param circleR               抠图凸起圆心
     * @param expireIn              滑块验证码默认过期时间, 单位: 秒
     * @param rectanglePadding      抠图内部矩形填充大小
     * @param sliderImgOutPadding   抠图的边框宽度
     * @return  sliderCode
     * @throws IOException  IOException
     */
    public static SliderCode getSliderCodeImage(String filePath, int cutWidth, int cutHeight, int circleR,
                                                int rectanglePadding, int sliderImgOutPadding, int expireIn) throws IOException {
        BufferedImage srcImage = ImageIO.read(new File(filePath));

        int locationX = cutWidth + new Random().nextInt(srcImage.getWidth() - cutWidth * 3);
        int locationY = cutHeight + new Random().nextInt(srcImage.getHeight() - cutHeight) / 2;
        BufferedImage markImage = new BufferedImage(cutWidth, cutHeight, BufferedImage.TYPE_4BYTE_ABGR);
        int[][] data = getBlockData(cutWidth, cutHeight, circleR, rectanglePadding, sliderImgOutPadding);
        cutImgByTemplate(srcImage, markImage, data, locationX, locationY, cutWidth, cutHeight);
        return new SliderCode(null, expireIn, null, getImageBASE64(markImage), getImageBASE64(srcImage), locationX, locationY,
                              srcImage.getWidth(), srcImage.getHeight());
    }


    /**
     * 生成随机滑块形状
     * <p>
     * 0 透明像素
     * 1 滑块像素
     * 2 阴影像素
     * @param cutWidth              模板图宽度
     * @param cutHeight             模板图高度
     * @param circleR               抠图凸起圆心
     * @param rectanglePadding      抠图内部矩形填充大小
     * @param sliderImgOutPadding   抠图的边框宽度
     * @return int[][]
     */
    @SuppressWarnings({"AlibabaAvoidComplexCondition", "AlibabaMethodTooLong", "AlibabaLowerCamelCaseVariableNaming"})
    private static int[][] getBlockData(int cutWidth, int cutHeight, int circleR,
                                        int rectanglePadding, int sliderImgOutPadding) {
        int[][] data = new int[cutWidth][cutHeight];
        Random random = new Random();
        //(x-a)²+(y-b)²=r²
        //x中心位置左右5像素随机
        double x1 = rectanglePadding + (cutWidth - 2 * rectanglePadding) / 2.0 - 5 + random.nextInt(10);
        //y 矩形上边界半径-1像素移动
        double y1_top = rectanglePadding - random.nextInt(3);
        double y1_bottom = cutHeight - rectanglePadding + random.nextInt(3);
        double y1 = random.nextInt(2) == 1 ? y1_top : y1_bottom;


        double x2_right = cutWidth - rectanglePadding - circleR + random.nextInt(2 * circleR - 4);
        double x2_left = rectanglePadding + circleR - 2 - random.nextInt(2 * circleR - 4);
        double x2 = random.nextInt(2) == 1 ? x2_right : x2_left;
        double y2 = rectanglePadding + (cutHeight - 2 * rectanglePadding) / 2.0 - 4 + random.nextInt(10);

        double po = Math.pow(circleR, 2);
        for (int i = 0; i < cutWidth; i++)
        {
            for (int j = 0; j < cutHeight; j++)
            {
                //矩形区域
                boolean fill;
                if ((i >= rectanglePadding && i < cutWidth - rectanglePadding)
                        && (j >= rectanglePadding && j < cutHeight - rectanglePadding))
                {
                    data[i][j] = 1;
                    fill = true;
                }
                else
                {
                    data[i][j] = 0;
                    fill = false;
                }
                //凸出区域
                double d3 = Math.pow(i - x1, 2) + Math.pow(j - y1, 2);
                if (d3 < po)
                {
                    data[i][j] = 1;
                }
                else
                {
                    if (!fill)
                    {
                        data[i][j] = 0;
                    }
                }
                //凹进区域
                double d4 = Math.pow(i - x2, 2) + Math.pow(j - y2, 2);
                if (d4 < po)
                {
                    data[i][j] = 0;
                }
            }
        }
        //边界阴影
        for (int i = 0; i < cutWidth; i++)
        {
            for (int j = 0; j < cutHeight; j++)
            {
                //四个正方形边角处理
                for (int k = 1; k <= sliderImgOutPadding; k++)
                {
                    //左上、右上
                    if (i >= rectanglePadding - k && i < rectanglePadding
                            && ((j >= rectanglePadding - k && j < rectanglePadding)
                            || (j >= cutHeight - rectanglePadding - k && j < cutHeight - rectanglePadding + 1)))
                    {
                        data[i][j] = 2;
                    }

                    //左下、右下
                    if (i >= cutWidth - rectanglePadding + k - 1 && i < cutWidth - rectanglePadding + 1)
                    {
                        for (int n = 1; n <= sliderImgOutPadding; n++)
                        {
                            //noinspection IfStatementMissingBreakInLoop
                            if (((j >= rectanglePadding - n && j < rectanglePadding)
                                    || (j >= cutHeight - rectanglePadding - n && j <= cutHeight - rectanglePadding)))
                            {
                                data[i][j] = 2;
                            }
                        }
                    }
                }

                if (data[i][j] == 1 && j - sliderImgOutPadding > 0 && data[i][j - sliderImgOutPadding] == 0)
                {
                    data[i][j - sliderImgOutPadding] = 2;
                }
                if (data[i][j] == 1 && j + sliderImgOutPadding > 0 && j + sliderImgOutPadding < cutHeight && data[i][j + sliderImgOutPadding] == 0)
                {
                    data[i][j + sliderImgOutPadding] = 2;
                }
                if (data[i][j] == 1 && i - sliderImgOutPadding > 0 && data[i - sliderImgOutPadding][j] == 0)
                {
                    data[i - sliderImgOutPadding][j] = 2;
                }
                if (data[i][j] == 1 && i + sliderImgOutPadding > 0 && i + sliderImgOutPadding < cutWidth && data[i + sliderImgOutPadding][j] == 0)
                {
                    data[i + sliderImgOutPadding][j] = 2;
                }
            }
        }
        return data;
    }

    /**
     * 裁剪区块
     * 根据生成的滑块形状，对原图和裁剪块进行变色处理
     *
     * @param oriImage    原图
     * @param targetImage 裁剪图
     * @param blockImage  滑块
     * @param x           裁剪点x
     * @param y           裁剪点y
     * @param cutWidth      模板图宽度
     * @param cutHeight     模板图高度
     */
    @SuppressWarnings({"AlibabaAvoidStartWithDollarAndUnderLineNaming", "AlibabaLowerCamelCaseVariableNaming"})
    private static void cutImgByTemplate(BufferedImage oriImage, BufferedImage targetImage, int[][] blockImage,
                                         int x, int y, int cutWidth, int cutHeight) {
        for (int i = 0; i < cutWidth; i++)
        {
            for (int j = 0; j < cutHeight; j++)
            {
                int _x = x + i;
                int _y = y + j;
                int rgbFlg = blockImage[i][j];
                int rgb_ori = oriImage.getRGB(_x, _y);
                // 原图中对应位置变色处理
                if (rgbFlg == 1)
                {
                    //抠图上复制对应颜色值
                    targetImage.setRGB(i, j, rgb_ori);
                    //原图对应位置颜色变化
                    oriImage.setRGB(_x, _y, Color.LIGHT_GRAY.getRGB());
                }
                else if (rgbFlg == 2)
                {
                    targetImage.setRGB(i, j, Color.WHITE.getRGB());
                    oriImage.setRGB(_x, _y, Color.GRAY.getRGB());
                }
                else if (rgbFlg == 0)
                {
                    //int alpha = 0;
                    targetImage.setRGB(i, j, rgb_ori & 0x00ffffff);
                }
            }

        }
    }


    /**
     * 随机获取一张图片对象
     *
     * @param path          path
     * @return BufferedImage
     * @throws IOException  IOException
     */
    public static BufferedImage getRandomImage(String path) throws IOException {
        File files = new File(path);
        File[] fileList = files.listFiles();
        List<String> fileNameList = new ArrayList<>();
        if (fileList != null && fileList.length != 0)
        {
            for (File tempFile : fileList)
            {
                if (tempFile.isFile() && tempFile.getName().endsWith(".jpg"))
                {
                    fileNameList.add(tempFile.getAbsolutePath().trim());
                }
            }
        }
        Random random = new Random();
        File imageFile = new File(fileNameList.get(random.nextInt(fileNameList.size())));
        return ImageIO.read(imageFile);
    }

    /**
     * 将IMG输出为文件
     *
     * @param image image
     * @param file  file
     * @throws Exception    Exception
     */
    public static void writeImg(BufferedImage image, String file) throws Exception {
        byte[] imageData;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        imageData = baos.toByteArray();
        try (FileOutputStream out = new FileOutputStream(new File(file));) {
            out.write(imageData);
        }
    }

    /**
     * 将图片转换为BASE64
     *
     * @param image     image
     * @return  图片字符串
     * @throws IOException  IOException
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static String getImageBASE64(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        //转成byte数组
        byte[] bytes = out.toByteArray();
        return Base64Utils.encodeToString(bytes);
    }

    /**
     * 将BASE64字符串转换为图片
     *
     * @param base64String  base64String
     * @return  BufferedImage
     */
    public static BufferedImage base64StringToImage(String base64String) {
        try
        {
            byte[] bytes = Base64Utils.decodeFromString(base64String);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            return ImageIO.read(byteArrayInputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
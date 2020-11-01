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

package demo.validate.code.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 简单的滑块验证码工厂, 建议自己自定义 {@link SliderCodeFactory}，使用带有缓存池的工厂, 并注入 IOC 容器, 会替代此类
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/21 21:58
 */
@Component("demoSimpleSliderCodeFactory")
@Slf4j
public class SimpleSliderCodeFactory implements SliderCodeFactory {

    /**
     * 抠图凸起圆心, 默认: 5
     */
    private static final int CIRCLE_R = 5;
    /**
     * 抠图内部矩形填充大小, 默认: 8
     */
    private static final int RECTANGLE_PADDING = 8;
    /**
     * 抠图的边框宽度, 默认: 1
     */
    private static final int SLIDER_IMG_OUT_PADDING = 1;
    /**
     * 模板图宽度
     */
    private static final int CUT_WIDTH = 50;
    /**
     * 模板图高度
     */
    private static final int CUT_HEIGHT = 50;
    /**
     * 滑块验证码默认过期时间, 180 秒
     */
    private static final int SLIDER_IMG_SLIDER_IMG_EXPIRE_IN = 180;

    /**
     * 原始图片存储的文件夹的相对路径(基于项目的)
     */
    private static final String RELATIVE_PATH = "static/image/validate/targets";

    @Override
    public SliderCode getSliderCode() {

        try
        {
            // 生产环境先生成好的验证码图片源, 在从缓存池中获取.
            SliderCode sliderCode = SliderCodeUtil.getSliderCodeImage(getRandomAbsPath(RELATIVE_PATH),
                                                                      CUT_WIDTH,
                                                                      CUT_HEIGHT,
                                                                      CIRCLE_R,
                                                                      RECTANGLE_PADDING,
                                                                      SLIDER_IMG_OUT_PADDING,
                                                                      SLIDER_IMG_SLIDER_IMG_EXPIRE_IN);

            sliderCode.setToken(ValidateCodeUtil.getUuid());

            return sliderCode;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ValidateCodeException(ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE, null, null);
        }
    }

    private String getRandomAbsPath(String originalImageDirectory) {

        File dirFile;
        try
        {
            dirFile = ResourceUtils.getFile("classpath:" + originalImageDirectory);
            String[] fileNames;
            if (dirFile.isDirectory())
            {
                fileNames = dirFile.list();
                if (fileNames != null)
                {
                    int r = ThreadLocalRandom.current().nextInt(fileNames.length);
                    return String.format("%s%s%s", dirFile.getPath(), "/", fileNames[r]);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            log.info(String.format( "产生 slider 验证码图片时找不到图片源, 图片源相对路径: %s", originalImageDirectory), e);
        }

        log.info("产生 slider 验证码图片时找不到图片源, 图片源相对路径: {}", originalImageDirectory);
        throw new RuntimeException("找不到图片源, 图片源相对路径: " + originalImageDirectory);

    }

}
package demo.validate.code.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import top.dcenter.ums.security.core.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 简单的滑块验证码工厂, 建议自己自定义 {@link SliderCodeFactory}，使用带有缓存池的工厂, 并注入 IOC 容器, 会替代此类
 * @author zyw
 * @version V1.0  Created by 2020/9/21 21:58
 */
@Component
@Slf4j
public class SimpleSliderCodeFactory implements SliderCodeFactory {

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
            SliderCode sliderCode = SliderCodeUtil.getSliderCodeImage(getRandomAbsPath(), CUT_WIDTH, CUT_HEIGHT, SLIDER_IMG_SLIDER_IMG_EXPIRE_IN);

            sliderCode.setToken(ValidateCodeUtil.getUUID());

            sliderCode.setExpireTime(SLIDER_IMG_SLIDER_IMG_EXPIRE_IN);
            return sliderCode;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ValidateCodeException(ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE, null, null);
        }
    }

    private String getRandomAbsPath() {

        File dirFile;
        try
        {
            dirFile = ResourceUtils.getFile("classpath:" + RELATIVE_PATH);
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
            log.info(String.format( "生产 slider 验证码图片时找不到图片源, 图片源相对路径: %s", RELATIVE_PATH), e);
        }

        log.info("生产 slider 验证码图片时找不到图片源, 图片源相对路径: {}", RELATIVE_PATH);
        throw new RuntimeException("找不到图片源, 图片源相对路径: " + RELATIVE_PATH);

    }

}

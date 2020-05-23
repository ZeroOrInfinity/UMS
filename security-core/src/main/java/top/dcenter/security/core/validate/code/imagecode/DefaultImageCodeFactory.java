package top.dcenter.security.core.validate.code.imagecode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.ServletRequestUtils;
import top.dcenter.security.core.api.validateCode.ImageCodeFactory;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.CodeUtil;
import top.dcenter.security.core.util.ImageUtil;

import javax.servlet.ServletRequest;
import java.awt.image.BufferedImage;

/**
 * 图片校验码工厂，默认实现，建议自己自定义 {@link ImageCodeFactory}，使用带有缓存池的工厂
 * @author zyw
 * @version V1.0  Created by 2020/5/22 11:23
 */
@Slf4j
public class DefaultImageCodeFactory implements ImageCodeFactory {

    private final ValidateCodeProperties validateCodeProperties;

    public DefaultImageCodeFactory(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public ImageCode getImageCode(ServletRequest request) {

        ValidateCodeProperties.ImageCodeProperties imageProp = this.validateCodeProperties.getImage();
        int w = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaWidthName(),
                                                    imageProp.getWidth());
        int h = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaHeightName(),
                                                    imageProp.getHeight());
        int expireIn = imageProp.getExpire();
        int codeLength = imageProp.getLength();

        String code = CodeUtil.generateVerifyCode(codeLength);

        BufferedImage bufferedImage = ImageUtil.getBufferedImage(w, h, code);
        return new ImageCode(bufferedImage, code, expireIn);
    }

}

package top.dcenter.security.core.validate.code.imagecode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.ServletRequestUtils;
import top.dcenter.security.core.api.validate.code.ImageCodeFactory;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.ValidateCodeUtil;
import top.dcenter.security.core.util.ImageUtil;

import javax.servlet.ServletRequest;
import java.awt.image.BufferedImage;

/**
 * 图片验证码工厂，默认实现，建议自己自定义 {@link ImageCodeFactory}，使用带有缓存池的工厂
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

        int width = imageProp.getWidth();
        int height = imageProp.getHeight();
        int w = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaWidthName(),
                                                    width);
        int h = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaHeightName(),
                                                    height);
        // 防止恶意图片攻击
        w = Math.min(width * 2, w);
        h = Math.min(height * 2, h);

        int expireIn = imageProp.getExpire();
        int codeLength = imageProp.getLength();

        String code = ValidateCodeUtil.generateVerifyCode(codeLength);

        BufferedImage bufferedImage = ImageUtil.getBufferedImage(w, h, code);
        return new ImageCode(bufferedImage, code, expireIn);
    }

}

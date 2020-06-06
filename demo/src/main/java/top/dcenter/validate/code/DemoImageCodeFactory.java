package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import top.dcenter.security.core.api.validate.code.ImageCodeFactory;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.ValidateCodeUtil;
import top.dcenter.security.core.util.ImageUtil;
import top.dcenter.security.core.auth.validate.codes.image.ImageCode;

import javax.servlet.ServletRequest;
import java.awt.image.BufferedImage;

/**
 * 图片验证码工厂，
 * @author zyw
 * @version V1.0  Created by 2020/5/22 11:23
 */
@Component
@Slf4j
public class DemoImageCodeFactory implements ImageCodeFactory {

    private final ValidateCodeProperties validateCodeProperties;

    public DemoImageCodeFactory(ValidateCodeProperties validateCodeProperties) {
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
        if (log.isDebugEnabled())
        {
            log.debug("Demo =====>: {} = {}", this.validateCodeProperties.getImage().getRequestParamImageCodeName(),
                      code);
        }
        return new ImageCode(bufferedImage, code, expireIn);
    }

}
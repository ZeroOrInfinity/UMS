package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import top.dcenter.security.core.api.validate.code.ImageCodeFactory;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.CodeUtil;
import top.dcenter.security.core.util.ImageUtil;
import top.dcenter.security.core.validate.code.imagecode.ImageCode;

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
        int w = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaWidthName(),
                                                    imageProp.getWidth());
        int h = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaHeightName(),
                                                    imageProp.getHeight());
        int expireIn = imageProp.getExpire();
        int codeLength = imageProp.getLength();

        String code = CodeUtil.generateVerifyCode(codeLength);

        BufferedImage bufferedImage = ImageUtil.getBufferedImage(w, h, code);
        if (log.isDebugEnabled())
        {
            log.debug("Demo =====>: {} = {}", this.validateCodeProperties.getImage().getRequestParamImageCodeName(),
                      code);
        }
        return new ImageCode(bufferedImage, code, expireIn);
    }

}
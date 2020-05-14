package top.dcenter.security.core.validate.code.imagecode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.ServletRequestUtils;
import top.dcenter.security.core.util.CodeUtil;
import top.dcenter.security.core.util.ImageUtil;
import top.dcenter.security.core.validate.code.ValidateCodeGenerator;
import top.dcenter.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;
import java.awt.image.BufferedImage;

/**
 * 图片验证码生成器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 23:44
 */
@Slf4j
public class ImageCodeGenerator implements ValidateCodeGenerator<ImageCode> {

    private final ValidateCodeProperties validateCodeProperties;

    public ImageCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public ImageCode generate(ServletRequest request) {
        ValidateCodeProperties.ImageCodeProperties imageProp = this.validateCodeProperties.getImage();
        int w = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaWidthName(),
                                                    imageProp.getWidth());
        int h = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaHeightName(),
                                                    imageProp.getHeight());
        int expireIn = imageProp.getExpire();
        int codeLength = imageProp.getLength();

        String code = CodeUtil.generateVerifyCode(codeLength);
        if (log.isDebugEnabled())
        {
            log.debug("{} = {}", imageProp.getRequestParamImageCodeName(), code);
        }
        BufferedImage bufferedImage = ImageUtil.getBufferedImage(w, h, code);
        return new ImageCode(bufferedImage, code, expireIn);
    }

    @Override
    public String getRequestParamValidateCodeName() {
        return validateCodeProperties.getImage().getRequestParamImageCodeName();
    }

}

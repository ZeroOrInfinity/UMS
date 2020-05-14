package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.CodeUtil;
import top.dcenter.security.core.util.ImageUtil;
import top.dcenter.security.core.validate.code.imagecode.ImageCode;
import top.dcenter.security.core.validate.code.imagecode.ImageCodeGenerator;

import javax.servlet.ServletRequest;
import java.awt.image.BufferedImage;

/**
 * 注意：实现类注册 ioc 容器 bean 的名称必须是 imageCodeGenerator
 * @author zyw
 * @createrDate 2020-05-14 22:24
 */
@Component()
@Slf4j
public class DemoImageCodeGenerator extends ImageCodeGenerator {

    private final ValidateCodeProperties validateCodeProperties;

    public DemoImageCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        super(validateCodeProperties);
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
            log.debug("Demo =====>: {} = {}", imageProp.getRequestParamImageCodeName(), code);
        }
        BufferedImage bufferedImage = ImageUtil.getBufferedImage(w, h, code);
        return new ImageCode(bufferedImage, code, expireIn);
    }

}
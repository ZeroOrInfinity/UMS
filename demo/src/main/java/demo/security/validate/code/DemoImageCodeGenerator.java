package demo.security.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.validate.code.ImageCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCode;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 推荐实现此接口 {@link ImageCodeFactory}。
 * @author zyw
 * @version V1.0  Created by  2020-05-14 22:24
 */
@Component()
@Slf4j
public class DemoImageCodeGenerator extends ImageCodeGenerator {

    private final ImageCodeFactory imageCodeFactory;
    private final ValidateCodeProperties validateCodeProperties;

    public DemoImageCodeGenerator(ImageCodeFactory imageCodeFactory, ValidateCodeProperties validateCodeProperties) {
        super(validateCodeProperties, imageCodeFactory);
        this.imageCodeFactory = imageCodeFactory;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public ImageCode generate(ServletRequest request) {
        ImageCode imageCode = imageCodeFactory.getImageCode(request);
        if (log.isDebugEnabled())
        {
            log.debug("Demo =====>: {} = {}", this.validateCodeProperties.getImage().getRequestParamImageCodeName(),
                      imageCode.getCode());
        }
        return imageCode;
    }

}
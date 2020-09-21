package top.dcenter.ums.security.core.auth.validate.codes.image;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.ums.security.core.api.validate.code.ImageCodeFactory;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeTokenFactory;
import top.dcenter.ums.security.core.enums.ValidateCodeType;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 图片验证码生成器。如要自定义图片验证码生成器，推荐实现此接口 {@link ImageCodeFactory}。注意：实现类注册 ioc 容器 bean 即可。<br><br>
 *     当然也可以继承后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/4 23:44
 */
@Slf4j
public class ImageCodeGenerator implements ValidateCodeGenerator<ImageCode> {

    protected final ValidateCodeProperties validateCodeProperties;
    protected final ImageCodeFactory imageCodeFactory;
    protected final ValidateCodeTokenFactory validateCodeTokenFactory;

    public ImageCodeGenerator(ValidateCodeProperties validateCodeProperties, ImageCodeFactory imageCodeFactory, ValidateCodeTokenFactory validateCodeTokenFactory) {
        this.validateCodeProperties = validateCodeProperties;
        this.imageCodeFactory = imageCodeFactory;
        this.validateCodeTokenFactory = validateCodeTokenFactory;
    }

    @Override
    public ImageCode generate(ServletRequest request) {
        return imageCodeFactory.getImageCode(request, validateCodeTokenFactory);
    }

    @Override
    public String getValidateCodeType() {
        return ValidateCodeType.IMAGE.name().toLowerCase();
    }

    @Override
    public String getRequestParamValidateCodeName() {
        return validateCodeProperties.getImage().getRequestParamImageCodeName();
    }

}

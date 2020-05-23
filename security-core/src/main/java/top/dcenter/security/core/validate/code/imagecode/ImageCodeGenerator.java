package top.dcenter.security.core.validate.code.imagecode;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.security.core.api.validateCode.ImageCodeFactory;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.api.validateCode.ValidateCodeGenerator;
import top.dcenter.security.core.validate.code.ValidateCodeType;

import javax.servlet.ServletRequest;

/**
 * 图片验证码生成器。如要自定义图片验证码生成器，请实现此接口 {@link ImageCodeFactory}。注意：实现类注册 ioc 容器 bean 即可。
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 23:44
 */
@Slf4j
public class ImageCodeGenerator implements ValidateCodeGenerator<ImageCode> {

    private final ValidateCodeProperties validateCodeProperties;
    private final ImageCodeFactory imageCodeFactory;

    public ImageCodeGenerator(ValidateCodeProperties validateCodeProperties, ImageCodeFactory imageCodeFactory) {
        this.validateCodeProperties = validateCodeProperties;
        this.imageCodeFactory = imageCodeFactory;
    }

    @Override
    public ImageCode generate(ServletRequest request) {
        return imageCodeFactory.getImageCode(request);
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

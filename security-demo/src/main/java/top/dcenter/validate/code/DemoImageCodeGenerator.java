package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.imagecode.ImageCode;
import top.dcenter.security.core.api.validateCode.ImageCodeFactory;
import top.dcenter.security.core.validate.code.imagecode.ImageCodeGenerator;

import javax.servlet.ServletRequest;

/**
 * 注意：实现类注册 ioc 容器 bean 的名称必须是 imageCodeGenerator
 * @author zyw
 * @createrDate 2020-05-14 22:24
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
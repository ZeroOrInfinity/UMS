package demo.validate.code.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 滑块验证码生成器
 * @author zyw
 * @version V1.0  Created by 2020/9/21 12:32
 */
@Component
@Slf4j
public class SliderValidateCodeGenerator implements ValidateCodeGenerator<SliderCode> {


    private final SliderCodeFactory sliderCodeFactory;

    private final ValidateCodeProperties validateCodeProperties;

    public SliderValidateCodeGenerator(SliderCodeFactory sliderCodeFactory, ValidateCodeProperties validateCodeProperties) {
        this.sliderCodeFactory = sliderCodeFactory;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public SliderCode generate(ServletRequest request) {

        SliderCode sliderCode = sliderCodeFactory.getSliderCode();
        log.info("Demo =====>: {} = {}", getValidateCodeType(),
                  sliderCode);
        return sliderCode;
    }

    @Override
    public String getValidateCodeType() {
        return ValidateCodeType.SLIDER.name().toLowerCase();
    }

    @Override
    public String getRequestParamValidateCodeName() {
        // 前端把第一次验证通过后的 token 设置到请求参数名称为 sliderToken 上.
        return validateCodeProperties.getSlider().getRequestParamName();
    }

}

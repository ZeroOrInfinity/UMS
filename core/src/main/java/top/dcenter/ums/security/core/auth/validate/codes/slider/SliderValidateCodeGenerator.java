package top.dcenter.ums.security.core.auth.validate.codes.slider;

import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.ums.security.core.api.validate.code.slider.SliderCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 滑块验证码生成器, 自定义生成器请继承此类且注入 IOC 容器即可
 * @author zyw
 * @version V1.0  Created by 2020/9/21 12:32
 */
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
        // 验证失败,不刷新验证码
        sliderCode.setReuse(true);
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

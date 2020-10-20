/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.validate.code.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 滑块验证码生成器
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/21 12:32
 */
@Component("demoSliderValidateCodeGenerator")
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
        // 验证失败,不刷新验证码
        sliderCode.setReuse(true);
        log.info("Demo =====>: {} = {}", getValidateCodeType(),
                  sliderCode);
        return sliderCode;
    }

    @Override
    public String getValidateCodeType() {
        return ValidateCodeType.CUSTOMIZE.name().toLowerCase();
    }

    @Override
    public String getRequestParamValidateCodeName() {
        // 前端把第一次验证通过后的 token 设置到请求参数名称为 sliderToken 上.
        return validateCodeProperties.getCustomize().getRequestParamName();
    }

}
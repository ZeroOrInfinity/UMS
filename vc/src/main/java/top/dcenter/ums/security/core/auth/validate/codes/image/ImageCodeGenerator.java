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

package top.dcenter.ums.security.core.auth.validate.codes.image;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.ums.security.core.api.validate.code.image.ImageCodeFactory;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 图片验证码生成器。如要自定义图片验证码生成器，推荐实现此接口 {@link ImageCodeFactory}。注意：实现类注册 ioc 容器 bean 即可。<br><br>
 *     当然也可以继承后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/4 23:44
 */
@Slf4j
public class ImageCodeGenerator implements ValidateCodeGenerator<ImageCode> {

    protected final ValidateCodeProperties validateCodeProperties;
    protected final ImageCodeFactory imageCodeFactory;

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
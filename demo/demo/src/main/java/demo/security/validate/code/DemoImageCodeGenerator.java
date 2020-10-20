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

package demo.security.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.validate.code.image.ImageCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCode;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 推荐实现此接口 {@link ImageCodeFactory}。
 * @author YongWu zheng
 * @version V1.0  Created by  2020-05-14 22:24
 */
@Component()
@Slf4j
public class DemoImageCodeGenerator extends ImageCodeGenerator {

    public DemoImageCodeGenerator(ImageCodeFactory imageCodeFactory,
                                  ValidateCodeProperties validateCodeProperties) {
        super(validateCodeProperties, imageCodeFactory);
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
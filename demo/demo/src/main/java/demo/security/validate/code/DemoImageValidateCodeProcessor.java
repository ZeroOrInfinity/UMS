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
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCode;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageValidateCodeProcessor;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义图片验证码处理器
 * @author YongWu zheng
 * @version V1.0  Created by 2020-05-14 23:52
 */
@Component
@Slf4j
public class DemoImageValidateCodeProcessor extends ImageValidateCodeProcessor {

    public DemoImageValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {
        super(validateCodeGeneratorHolder);
    }

    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        try
        {
            if (!(validateCode instanceof ImageCode))
            {
                return false;
            }
            ImageCode imageCode = (ImageCode) validateCode;

            HttpServletResponse response = request.getResponse();
            if (response == null)
            {
                return false;
            }
            ImageIO.write(imageCode.getImage(), "JPG", response.getOutputStream());
            log.info("Demo ========>: imageCode = {}", imageCode);
            return true;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
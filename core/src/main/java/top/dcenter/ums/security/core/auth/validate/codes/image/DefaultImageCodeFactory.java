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
import org.springframework.web.bind.ServletRequestUtils;
import top.dcenter.ums.security.core.api.validate.code.image.ImageCodeFactory;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

import javax.servlet.ServletRequest;
import java.awt.image.BufferedImage;

/**
 * 图片验证码工厂，默认实现，建议自己自定义 {@link ImageCodeFactory}，使用带有缓存池的工厂, 并注入 IOC 容器, 会替代此类
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/22 11:23
 */
@Slf4j
public class DefaultImageCodeFactory implements ImageCodeFactory {

    private final ValidateCodeProperties validateCodeProperties;

    public DefaultImageCodeFactory(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public ImageCode getImageCode(ServletRequest request) {

        ValidateCodeProperties.ImageCodeProperties imageProp = this.validateCodeProperties.getImage();

        int width = imageProp.getWidth();
        int height = imageProp.getHeight();
        int w = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaWidthName(),
                                                    width);
        int h = ServletRequestUtils.getIntParameter(request, imageProp.getRequestParaHeightName(),
                                                    height);
        // 防止恶意图片攻击
        w = Math.min(width * 2, w);
        h = Math.min(height * 2, h);

        int expireIn = imageProp.getExpire();
        int codeLength = imageProp.getLength();

        String code = ValidateCodeUtil.generateVerifyCode(codeLength);

        BufferedImage bufferedImage = ImageUtil.getBufferedImage(w, h, code);
        return new ImageCode(bufferedImage, code, expireIn);

    }

}
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

package top.dcenter.ums.security.core.api.validate.code.image;

import top.dcenter.ums.security.core.auth.validate.codes.image.DefaultImageCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCode;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 图片验证码生成器。如要自定义图片验证码生成器，请实现此类。<br><br>
 *     注意：自定义实现类注册 ioc 容器，会自动覆盖 {@link DefaultImageCodeFactory}，建议实现 {@link ImageCodeFactory}时，使用带有缓存池的工厂
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/5/22 11:14
 */
public interface ImageCodeFactory {
    /**
     * 获取图片验证码. <br><br>
     *     如果 request 中传递了图片的宽与高的值，参数名称必须与 {@link ValidateCodeProperties} 中的 ums.codes.image
     *     .request-para-height-name=height 和 ums.codes.image.request-para-width-name=width 一致。
     *     如果 request 中没有传递相关参数，则会使用 ums.codes.image.height=60 和 ums.codes.image.width=270 默认配置。<br><br>
     *     以上参数都可以自定义配置。
     * @param request                   request
     * @return 图片验证码
     */
    ImageCode getImageCode(ServletRequest request);
}
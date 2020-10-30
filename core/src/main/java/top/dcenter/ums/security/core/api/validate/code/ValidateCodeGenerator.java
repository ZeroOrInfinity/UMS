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

package top.dcenter.ums.security.core.api.validate.code;

import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsCodeGenerator;

import javax.servlet.ServletRequest;

/**
 * 权限认证验证码生成接口。默认实现 {@link ImageCodeGenerator} 与
 * {@link SmsCodeGenerator}<br><br>
 * 在 {@link ValidateCodeType} 中还定义了其他未实现的常见验证码：<br><br>
 *     {@link ValidateCodeType#SELECTION}，<br><br>
 *     {@link ValidateCodeType#SLIDER}，<br><br>
 *     {@link ValidateCodeType#TRACK}，<br><br>
 *     {@link ValidateCodeType#CUSTOMIZE}。<br><br>
 * 自定义生成验证码逻辑时：<br><br>
 *  1. 实现此验证码生成器接口，<br><br>
 *  2. 如果要覆盖已有的验证码逻辑，继承他，再向 IOC 容器注册自己。
 *
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0
 * Created by 2020/5/4 23:14
 */
public interface ValidateCodeGenerator<T> {
    /**
     * 生成验证码
     * @param request   获取验证码请求
     * @return  返回验证码对象
     */
    T generate(ServletRequest request);

    /**
     * 获取验证码类型
     * @return  ValidateCodeType 的小写字符串
     */
    String getValidateCodeType();

    /**
     * 获取请求中的验证码参数的名称
     * @return  返回验证码参数的名称
     */
    String getRequestParamValidateCodeName();

}
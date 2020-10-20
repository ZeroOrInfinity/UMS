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

package top.dcenter.ums.security.core.auth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE;

/**
 * 短信验证码登录属性
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/3 19:51
 */
@Getter
@Setter
@ConfigurationProperties("ums.mobile.login")
public class SmsCodeLoginAuthenticationProperties {
    /**
     * 手机验证码登录请求处理url, 默认 /authentication/mobile
     */
    public String loginProcessingUrlMobile = DEFAULT_LOGIN_PROCESSING_URL_MOBILE;

    /**
     * 手机验证码登录是否开启, 默认 false
     */
    public Boolean smsCodeLoginIsOpen = false;



}
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
package top.dcenter.ums.security.common.propertis;

import org.springframework.boot.convert.DurationUnit;
import org.springframework.security.web.authentication.RememberMeServices;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;

/**
 * security 客户端配置属性
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/3 19:51
 */
public class RememberMeProperties {

    /**
     * RememberMe 是否开启, 默认为 false;
     */
    private Boolean enable = false;

    /**
     * 设置记住我功能的 session 的缓存时长，默认 14 天. If a duration suffix is not specified, seconds will be used.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration rememberMeTimeout = Duration.parse("P14D");

    /**
     * 设置记住我功能的 CookieName，默认 REMEMBER_ME, 自定义 {@link RememberMeServices} 时, 此配置失效
     */
    private String rememberMeCookieName = DEFAULT_REMEMBER_ME_NAME;
    /**
     * 设置记住我功能的参数名称，默认 REMEMBER_ME
     */
    private String rememberMeParameter = DEFAULT_REMEMBER_ME_NAME;

    /**
     * Whether the cookie should be flagged as secure or not. Secure cookies can only be sent over an HTTPS connection and thus cannot be accidentally submitted over HTTP where they could be intercepted.
     * By default the cookie will be secure if the request is secure. If you only want to use remember-me over HTTPS (recommended) you should set this property to true. 默认为 false。
     */
    private Boolean useSecureCookie = false;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Duration getRememberMeTimeout() {
        return rememberMeTimeout;
    }

    public void setRememberMeTimeout(Duration rememberMeTimeout) {
        this.rememberMeTimeout = rememberMeTimeout;
    }

    public String getRememberMeCookieName() {
        return rememberMeCookieName;
    }

    public void setRememberMeCookieName(String rememberMeCookieName) {
        this.rememberMeCookieName = rememberMeCookieName;
    }

    public String getRememberMeParameter() {
        return rememberMeParameter;
    }

    public void setRememberMeParameter(String rememberMeParameter) {
        this.rememberMeParameter = rememberMeParameter;
    }

    public Boolean getUseSecureCookie() {
        return useSecureCookie;
    }

    public void setUseSecureCookie(Boolean useSecureCookie) {
        this.useSecureCookie = useSecureCookie;
    }
}
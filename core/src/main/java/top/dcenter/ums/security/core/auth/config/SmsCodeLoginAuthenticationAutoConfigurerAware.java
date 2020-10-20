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

package top.dcenter.ums.security.core.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.auth.properties.SmsCodeLoginAuthenticationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.POST;
import static top.dcenter.ums.security.common.bean.UriHttpMethodTuple.tuple;

/**
 * 手机登录配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/15 21:51
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.mobile.login", name = "sms-code-login-is-open", havingValue = "true")
@AutoConfigureAfter({SmsCodeLoginAutoAuthenticationConfigurer.class})
@Slf4j
public class SmsCodeLoginAuthenticationAutoConfigurerAware implements HttpSecurityAware {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SmsCodeLoginAutoAuthenticationConfigurer smsCodeLoginAutoAuthenticationConfigurer;
    private final SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties;

    public SmsCodeLoginAuthenticationAutoConfigurerAware(SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties) {
        this.smsCodeLoginAuthenticationProperties = smsCodeLoginAuthenticationProperties;
    }

    @Override
    public void configure(WebSecurity web) {
        // dto nothing
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // dto nothing
    }

    @Override
    public void postConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 短信验证码登录配置
        if (smsCodeLoginAutoAuthenticationConfigurer != null)
        {
            http.apply(smsCodeLoginAutoAuthenticationConfigurer);
        }
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(tuple(POST, smsCodeLoginAuthenticationProperties.getLoginProcessingUrlMobile()), null);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }
}
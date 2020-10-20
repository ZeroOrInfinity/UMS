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

package demo.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import top.dcenter.ums.security.social.config.SocialCoreConfig;
import top.dcenter.ums.security.social.properties.SocialProperties;

/**
 * 自定义第三方授权登录核心配置 SocialCoreConfig，注意: 覆写方法 {@link #postProcess(Object)} 时一定要调用
 * <code>
 *     super.postProcess(object);
 * </code>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/14 20:06
 */
@SuppressWarnings("jol")
@Configuration
@Slf4j
public class DemoSocialConfig extends SocialCoreConfig {

    private final SocialProperties socialProperties;
    private final AuthenticationFailureHandler clientAuthenticationFailureHandler;

    public DemoSocialConfig(SocialProperties socialProperties, SocialProperties socialProperties1, AuthenticationFailureHandler clientAuthenticationFailureHandler) {
        super(socialProperties);
        this.socialProperties = socialProperties1;
        this.clientAuthenticationFailureHandler = clientAuthenticationFailureHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T postProcess(T object) {
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);

        filter.setFilterProcessesUrl(socialProperties.getCallbackUrl());
        filter.setSignupUrl(socialProperties.getSignUpUrl());
        filter.setDefaultFailureUrl(socialProperties.getFailureUrl());
        log.info("Demo ======>: DemoSocialConfig.postProcess");
        return (T) filter;
    }

}
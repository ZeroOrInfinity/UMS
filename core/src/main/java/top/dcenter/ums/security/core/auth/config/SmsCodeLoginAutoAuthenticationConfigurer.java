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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.core.auth.mobile.SmsCodeLoginAuthenticationFilter;
import top.dcenter.ums.security.core.auth.mobile.SmsCodeLoginAuthenticationProvider;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.auth.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;

import javax.servlet.http.HttpServletRequest;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.registerHandlerAndRememberMeServices;


/**
 * 短信登录配置
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/7 23:32
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.mobile.login", name = "sms-code-login-is-open", havingValue = "true")
@AutoConfigureAfter({SecurityAutoConfiguration.class})
public class SmsCodeLoginAutoAuthenticationConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ValidateCodeProperties validateCodeProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler;
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private UmsUserDetailsService userDetailsService;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private PersistentTokenRepository persistentTokenRepository;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private TenantContextHolder tenantContextHolder;
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private RememberMeServices rememberMeServices;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private GenerateClaimsSetService generateClaimsSetService;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private ClientProperties clientProperties;
    private final SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties;

    public SmsCodeLoginAutoAuthenticationConfigurer(ValidateCodeProperties validateCodeProperties,
                                                    BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                    BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                                    SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties) {
        this.validateCodeProperties = validateCodeProperties;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.smsCodeLoginAuthenticationProperties = smsCodeLoginAuthenticationProperties;
    }

    @Override
    public void configure(HttpSecurity http) {

        SmsCodeLoginAuthenticationFilter smsCodeLoginAuthenticationFilter =
                new SmsCodeLoginAuthenticationFilter(validateCodeProperties, smsCodeLoginAuthenticationProperties,
                                                     tenantContextHolder, authenticationDetailsSource);
        smsCodeLoginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        registerHandlerAndRememberMeServices(smsCodeLoginAuthenticationFilter,
                                             baseAuthenticationSuccessHandler,
                                             baseAuthenticationFailureHandler,
                                             persistentTokenRepository,
                                             userDetailsService,
                                             rememberMeServices,
                                             clientProperties.getRememberMe());

        SmsCodeLoginAuthenticationProvider smsCodeLoginAuthenticationProvider =
                new SmsCodeLoginAuthenticationProvider(userDetailsService, generateClaimsSetService);
        http.authenticationProvider(postProcess(smsCodeLoginAuthenticationProvider))
            .addFilterAfter(postProcess(smsCodeLoginAuthenticationFilter), AbstractPreAuthenticatedProcessingFilter.class);

    }

}
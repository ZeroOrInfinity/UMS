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

package top.dcenter.ums.security.core.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.common.api.userdetails.converter.AuthenticationToUserDetailsConverter;
import top.dcenter.ums.security.common.propertis.RememberMeProperties;
import top.dcenter.ums.security.common.utils.AppContextUtil;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.oauth.service.Auth2UserService;
import top.dcenter.ums.security.core.api.oauth.signup.ConnectionService;
import top.dcenter.ums.security.core.api.oauth.state.service.Auth2StateCoder;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.core.oauth.filter.login.Auth2LoginAuthenticationFilter;
import top.dcenter.ums.security.core.oauth.filter.redirect.Auth2DefaultRequestRedirectFilter;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.provider.Auth2LoginAuthenticationProvider;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;

/**
 * 添加 OAuth2(JustAuth) 配置
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/12 12:31
 */
@SuppressWarnings("jol")
@Configuration
@ConditionalOnProperty(prefix = "ums.oauth", name = "enabled", havingValue = "true")
@AutoConfigureAfter({Auth2AutoConfiguration.class})
public class Auth2AutoConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final Auth2Properties auth2Properties;
    private final UmsUserDetailsService umsUserDetailsService;
    private final Auth2UserService auth2UserService;
    private final ConnectionService connectionSignUp;
    private final ExecutorService updateConnectionTaskExecutor;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler;
    private final RedisConnectionFactory redisConnectionFactory;
    private final AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter;
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Auth2StateCoder auth2StateCoder;
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
    private RememberMeProperties rememberMe;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private RememberMeServices rememberMeServices;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private GenerateClaimsSetService generateClaimsSetService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public Auth2AutoConfigurer(Auth2Properties auth2Properties, UmsUserDetailsService umsUserDetailsService,
                               Auth2UserService auth2UserService,
                               ConnectionService connectionSignUp,
                               @Qualifier("updateConnectionTaskExecutor") ExecutorService updateConnectionTaskExecutor,
                               BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                               BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                               @Autowired(required = false)
                               RedisConnectionFactory redisConnectionFactory,
                               @Autowired(required = false)
                               AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter) {
        this.auth2Properties = auth2Properties;
        this.umsUserDetailsService = umsUserDetailsService;
        this.auth2UserService = auth2UserService;
        this.connectionSignUp = connectionSignUp;
        this.updateConnectionTaskExecutor = updateConnectionTaskExecutor;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.redisConnectionFactory = redisConnectionFactory;
        this.baseAuthenticationSuccessHandler.setAuth2RedirectUrl(auth2Properties.getRedirectUrlPrefix());
        this.authenticationToUserDetailsConverter = authenticationToUserDetailsConverter;

    }

    @Override
    public void configure(HttpSecurity http) {

        // 添加第三方登录入口过滤器
        String authorizationRequestBaseUri = auth2Properties.getAuthLoginUrlPrefix();
        Auth2DefaultRequestRedirectFilter auth2DefaultRequestRedirectFilter =
                new Auth2DefaultRequestRedirectFilter(authorizationRequestBaseUri, this.auth2StateCoder,
                                                      tenantContextHolder, baseAuthenticationFailureHandler);
        http.addFilterBefore(postProcess(auth2DefaultRequestRedirectFilter), AbstractPreAuthenticatedProcessingFilter.class);

        // 添加第三方登录回调接口过滤器
        String filterProcessesUrl = auth2Properties.getRedirectUrlPrefix();
        Auth2LoginAuthenticationFilter auth2LoginAuthenticationFilter =
                new Auth2LoginAuthenticationFilter(filterProcessesUrl, auth2Properties.getSignUpUrl(),
                                                   authenticationDetailsSource, redisConnectionFactory);
        AuthenticationManager sharedObject = http.getSharedObject(AuthenticationManager.class);
        auth2LoginAuthenticationFilter.setAuthenticationManager(sharedObject);
        //noinspection ConstantConditions
        if (baseAuthenticationFailureHandler != null)
        {
            // 添加认证失败处理器
            auth2LoginAuthenticationFilter.setAuthenticationFailureHandler(baseAuthenticationFailureHandler);
        }
        if (baseAuthenticationSuccessHandler != null)
        {
            // 添加认证成功处理器
            auth2LoginAuthenticationFilter.setAuthenticationSuccessHandler(baseAuthenticationSuccessHandler);
        }

        AppContextUtil.registerHandlerAndRememberMeServices(auth2LoginAuthenticationFilter,
                                                            baseAuthenticationSuccessHandler,
                                                            baseAuthenticationFailureHandler,
                                                            persistentTokenRepository,
                                                            userDetailsService,
                                                            rememberMeServices,
                                                            rememberMe);
        http.addFilterBefore(postProcess(auth2LoginAuthenticationFilter), Auth2DefaultRequestRedirectFilter.class);

        // 添加 provider
        Auth2LoginAuthenticationProvider auth2LoginAuthenticationProvider = new Auth2LoginAuthenticationProvider(
                auth2UserService, connectionSignUp, umsUserDetailsService,
                updateConnectionTaskExecutor, auth2Properties.getAutoSignUp(), generateClaimsSetService,
                auth2Properties.getTemporaryUserAuthorities(), auth2Properties.getTemporaryUserPassword(),
                authenticationToUserDetailsConverter);
        http.authenticationProvider(postProcess(auth2LoginAuthenticationProvider));
    }

}
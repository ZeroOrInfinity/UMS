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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.api.logout.DefaultLogoutSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.provider.UsernamePasswordAuthenticationProvider;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static top.dcenter.ums.security.common.bean.UriHttpMethodTuple.tuple;

/**
 * 客户端安全配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/2 17:30
 */
@Configuration
@Order(100)
@AutoConfigureAfter({SecurityAutoConfiguration.class})
public class ClientAutoConfigurerAware implements HttpSecurityAware {

    /**
     *  网站图标
     */
    public static final String FAVICON = "/**/favicon.ico";
    /**
     *  js
     */
    public static final String JS = "/**/*.js";
    /**
     *  css
     */
    public static final String CSS = "/**/*.css";
    /**
     *  html
     */
    public static final String  HTML = "/**/*.html";

    private final ClientProperties clientProperties;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired(required = false)
    private UmsUserDetailsService umsUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    private final DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;

    public ClientAutoConfigurerAware(ClientProperties clientProperties,
                                      BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                      BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                      PasswordEncoder passwordEncoder,
                                      UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider,
                                      DefaultLogoutSuccessHandler defaultLogoutSuccessHandler) {
        this.clientProperties = clientProperties;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.usernamePasswordAuthenticationProvider = usernamePasswordAuthenticationProvider;
        this.defaultLogoutSuccessHandler = defaultLogoutSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(WebSecurity web) {
        String[] ignoringUrls = clientProperties.getIgnoringUrls();
        web.ignoring()
                .antMatchers(Objects.requireNonNullElseGet(ignoringUrls, () -> new String[0]));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (umsUserDetailsService == null)
        {
            throw new RuntimeException("必须实现 UmsUserDetailsService 或 top.dcenter.security.social.api.service.UmsSocialUserDetailsService 抽象类");
        }
        auth.userDetailsService(umsUserDetailsService).passwordEncoder(passwordEncoder);
        auth.eraseCredentials(true);
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // 匿名用户配置
        anonymousConfigurer(http);

        // 允许来自同一来源(如: example.com)的请求
        if (clientProperties.getSameOrigin())
        {
            http.headers().frameOptions().sameOrigin();
        }
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 判断是否开启根据不同的uri跳转到相对应的登录页, 假设开启
        String loginUnAuthenticationRoutingUrl = clientProperties.getLoginUnAuthenticationRoutingUrl();
        if (!clientProperties.getOpenAuthenticationRedirect())
        {
            // 没有开启根据不同的uri跳转到相对应的登录页, 直接跳转到登录页
            loginUnAuthenticationRoutingUrl = clientProperties.getLogoutUrl();
        }
        /* 用户密码登录的 Provider, 只是对 org.springframework.security.auth.dao.DaoAuthenticationProvider 的 copy.
         * 替换 org.springframework.security.auth.dao.DaoAuthenticationProvider 的一个原因是:
         * 当有 IOC 容器中有多个 UserDetailsService 时, org.springframework.security.auth.dao
         * .DaoAuthenticationProvider 会失效.
         * 如果要对前端传过来的密码进行解密,则请实现 UserDetailsPasswordService
         */
        http.authenticationProvider(usernamePasswordAuthenticationProvider)
                .formLogin()
                .usernameParameter(clientProperties.usernameParameter)
                .passwordParameter(clientProperties.passwordParameter)
                .loginPage(loginUnAuthenticationRoutingUrl)
                // uri 需要自己实现
                .failureUrl(clientProperties.getFailureUrl())
                .defaultSuccessUrl(clientProperties.getSuccessUrl())
                // 由 Spring Security 接管，不用任何处理
                .loginProcessingUrl(clientProperties.getLoginProcessingUrl())
                // 语句位置更重要, 放在 failureUrl()与defaultSuccessUrl()之前会失效
                .successHandler(baseAuthenticationSuccessHandler)
                .failureHandler(baseAuthenticationFailureHandler);

        // logout
        logoutConfigurer(http);

    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {

        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);


        permitAllMap.put(tuple(GET, FAVICON), null);
        permitAllMap.put(tuple(GET, JS), null);
        permitAllMap.put(tuple(GET, CSS), null);
        permitAllMap.put(tuple(GET, HTML), null);
        permitAllMap.put(tuple(GET, clientProperties.getFailureUrl()), null);
        permitAllMap.put(tuple(GET, clientProperties.getLoginPage()), null);
        permitAllMap.put(tuple(GET, clientProperties.getLoginUnAuthenticationRoutingUrl()), null);
        permitAllMap.put(tuple(POST, clientProperties.getLoginProcessingUrl()), null);
        permitAllMap.put(tuple(GET, clientProperties.getErrorUrl()), null);
        permitAllMap.put(tuple(GET, clientProperties.getError4Url()), null);
        permitAllMap.put(tuple(GET, clientProperties.getError5Url()), null);

        Set<String> permitUrls = clientProperties.getPermitUrls();
        permitUrlsFillingPermitAllMap(permitUrls, permitAllMap);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }


    private void anonymousConfigurer(HttpSecurity http) throws Exception {
        ClientProperties.AnonymousProperties anonymous = clientProperties.getAnonymous();
        String[] authorities = new String[anonymous.getAuthorities().size()];
        anonymous.getAuthorities().toArray(authorities);
        if (anonymous.getAnonymousIsOpen())
        {
            http.anonymous()
                    .principal(anonymous.getPrincipal())
                    .authorities(authorities);
        }
        else
        {
            http.anonymous().disable();
        }
    }

    private void logoutConfigurer(HttpSecurity http) throws Exception {
        http.logout()
                .logoutUrl(clientProperties.getLogoutUrl())
                .logoutSuccessHandler(defaultLogoutSuccessHandler)
                .logoutSuccessUrl(clientProperties.getLogoutSuccessUrl())
                .deleteCookies(clientProperties.getRememberMe().getRememberMeCookieName(),
                               clientProperties.getSession().getSessionCookieName())
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll();
    }


}
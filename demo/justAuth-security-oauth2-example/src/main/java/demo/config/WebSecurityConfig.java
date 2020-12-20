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

package demo.config;

import demo.handler.DemoSignUpUrlAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import top.dcenter.ums.security.core.oauth.config.Auth2AutoConfigurer;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;

/**
 * web security config
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/18 22:39
 */
@SuppressWarnings("jol")
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Auth2AutoConfigurer auth2AutoConfigurer;
    @Autowired
    private Auth2Properties auth2Properties;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
            .loginPage("/login.html")
            .successHandler(this.authenticationSuccessHandler);

        http.logout().logoutSuccessUrl("/login.html");

        http.csrf().disable();

        // ========= start: 使用 justAuth-spring-security-starter 必须步骤 =========
        // 添加 Auth2AutoConfigurer 使 OAuth2(justAuth) login 生效.
        http.apply(this.auth2AutoConfigurer);

        // 放行第三方登录入口地址与第三方登录回调地址
        // @formatter:off
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET,
                             auth2Properties.getRedirectUrlPrefix() + "/*",
                             auth2Properties.getAuthLoginUrlPrefix() + "/*")
                .permitAll();
        // @formatter:on
        // ========= end: 使用 justAuth-spring-security-starter 必须步骤 =========

        http.authorizeRequests().anyRequest().permitAll();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        DemoSignUpUrlAuthenticationSuccessHandler demoSignUpUrlAuthenticationSuccessHandler = new DemoSignUpUrlAuthenticationSuccessHandler();
        demoSignUpUrlAuthenticationSuccessHandler.setDefaultTargetUrl("/index.html");
        this.authenticationSuccessHandler = demoSignUpUrlAuthenticationSuccessHandler;
        return demoSignUpUrlAuthenticationSuccessHandler;
    }
}
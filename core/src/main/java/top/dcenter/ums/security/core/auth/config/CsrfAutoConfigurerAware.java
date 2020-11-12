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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;

import java.util.Map;
import java.util.Set;

/**
 * csrf 配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/7 23:51
 */
@Configuration
@AutoConfigureAfter(value = {SecurityCsrfAutoConfiguration.class, PropertiesAutoConfiguration.class})
public class CsrfAutoConfigurerAware implements HttpSecurityAware {

    private final ClientProperties clientProperties;

    public CsrfAutoConfigurerAware(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
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

    @SuppressWarnings("RedundantThrows")
    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        ClientProperties.CsrfProperties csrfProperties = clientProperties.getCsrf();
        if (csrfProperties.getCsrfIsOpen())
        {
            CsrfConfigurer<HttpSecurity> csrf = http.csrf();

            Set<String> ignoringAntMatcherUrls = csrfProperties.getIgnoringAntMatcherUrls();
            String[] urls = new String[ignoringAntMatcherUrls.size()];
            ignoringAntMatcherUrls.toArray(urls);

            csrf.ignoringAntMatchers(urls);

        } else
        {
            http.csrf().disable();
        }

    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        return null;
    }
}
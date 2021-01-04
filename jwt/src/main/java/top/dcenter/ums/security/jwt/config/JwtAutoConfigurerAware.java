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
package top.dcenter.ums.security.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import top.dcenter.ums.security.common.access.UmsAccessDeniedHandlerImpl;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.jwt.filter.JwtExceptionOnceFilter;

import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * jwt 安全配置
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.5 14:36
 */
@Configuration
@AutoConfigureAfter({JwtAutoConfiguration.class})
@ConditionalOnProperty(prefix = "ums.jwt", name = "enable", havingValue = "true")
public class JwtAutoConfigurerAware implements HttpSecurityAware {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final JwtBearerTokenAuthenticationConverter jwtBearerTokenAuthenticationConverter;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public JwtAutoConfigurerAware(JwtDecoder jwtDecoder,
                                  @Autowired(required = false) JwtBearerTokenAuthenticationConverter jwtBearerTokenAuthenticationConverter,
                                  @Autowired(required = false) JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.jwtBearerTokenAuthenticationConverter = jwtBearerTokenAuthenticationConverter;
    }

    @Override
    public void configure(WebSecurity web) {
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {

        // 放在 WebAsyncManagerIntegrationFilter 后面, 则必定在 MdcLogFilter 后面, 方便获取 mdc 链路追踪 ID
        http.addFilterAfter(new JwtExceptionOnceFilter(), WebAsyncManagerIntegrationFilter.class);

        final OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer jwt =
                http.oauth2ResourceServer()
                    .accessDeniedHandler(new UmsAccessDeniedHandlerImpl())
                    .jwt();
        if (nonNull(this.jwtAuthenticationConverter)) {
            jwt.jwtAuthenticationConverter(this.jwtAuthenticationConverter);
        }
        if (nonNull(this.jwtBearerTokenAuthenticationConverter)) {
            jwt.jwtAuthenticationConverter(this.jwtBearerTokenAuthenticationConverter);
        }
        jwt.decoder(this.jwtDecoder);

    }

    @Override
    public void postConfigure(HttpSecurity http) {
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        return null;
    }
}

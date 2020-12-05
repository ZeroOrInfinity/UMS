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
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * 跨域自动配置
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.4 21:16
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class CorsAutoConfigurationAware implements HttpSecurityAware {

    private final ClientProperties.CorsProperties corsProperties;

    public CorsAutoConfigurationAware(ClientProperties clientProperties) {
        this.corsProperties = clientProperties.getCors();
    }

    @Override
    public void configure(WebSecurity web) {
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
    }

    @Override
    public void preConfigure(HttpSecurity http) {
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // 跨域配置
        if (corsProperties.getEnable()) {
            http.cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .headers()
                .addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
                        // 支持指定源的访问
                        new Header("Access-control-Allow-Origin",
                                   this.corsProperties.getAccessControlAllowOrigin().toArray(new String[0])),
                        // 使 ajax 请求能够取到指定 header 中的信息
                        new Header("Access-Control-Expose-Headers",
                                   this.corsProperties.getAccessControlExposeHeaders())
                )));
        }
        else {
            http.cors().disable();
        }

    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        return null;
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(requireNonNull(corsProperties.getAccessControlAllowOrigin(),
                                                       "AccessControlAllowOrigin must be not null"));
        configuration.setAllowedMethods(requireNonNull(corsProperties.getAccessControlAllowMethods(),
                                                       "AccessControlAllowMethods must be not null"));
        configuration.setAllowedHeaders(requireNonNull(corsProperties.getAccessControlAllowHeaders(),
                                                       "AccessControlAllowHeaders must be not null"));
        configuration.addExposedHeader(requireNonNull(corsProperties.getAccessControlExposeHeaders(),
                                                      "AccessControlExposeHeaders must be not null"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        requireNonNull(corsProperties.getUrlList(), "urlList must be not null")
                .forEach(uri -> source.registerCorsConfiguration(uri, configuration));

        return source;
    }
}

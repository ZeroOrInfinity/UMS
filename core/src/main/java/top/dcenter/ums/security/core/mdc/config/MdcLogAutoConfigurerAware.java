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
package top.dcenter.ums.security.core.mdc.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.mdc.filter.MdcLogFilter;
import top.dcenter.ums.security.core.mdc.properties.MdcProperties;

import java.util.Map;
import java.util.Set;

/**
 * 基于SLF4J MDC机制实现日志的链路追踪: MVC配置
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/31 18:15
 */
@Configuration
@AutoConfigureAfter(value = {MdcPropertiesAutoConfiguration.class})
public class MdcLogAutoConfigurerAware implements HttpSecurityAware {

    private final MdcProperties mdcProperties;
    private final ClientProperties clientProperties;

    public MdcLogAutoConfigurerAware(MdcProperties mdcProperties, ClientProperties clientProperties) {
        this.mdcProperties = mdcProperties;
        this.clientProperties = clientProperties;
    }

    @Override
    public void configure(WebSecurity web) {
        // do nothing
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // do nothing
    }

    @Override
    public void postConfigure(HttpSecurity http) {
        if (this.mdcProperties.getEnable()) {
            // 基于 MDC 机制实现日志的链路追踪过滤器
            http.addFilterBefore(new MdcLogFilter(this.mdcProperties, this.clientProperties),
                                 WebAsyncManagerIntegrationFilter.class);
        }
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        // do nothing
        return null;
    }
}

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

package top.dcenter.ums.security.core.permission.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.auth.config.PropertiesAutoConfiguration;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.common.access.UmsAccessDeniedHandlerImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static top.dcenter.ums.security.common.bean.UriHttpMethodTuple.tuple;

/**
 * 权限相关配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/15 21:59
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
@Slf4j
public class UriAuthorizeAutoConfigurerAware implements HttpSecurityAware, ApplicationContextAware {

    private final ClientProperties  clientProperties;
    private ApplicationContext applicationContext;

    public UriAuthorizeAutoConfigurerAware(ClientProperties clientProperties) {
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

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // 设置授权异常处理器
        http.exceptionHandling().accessDeniedHandler(new UmsAccessDeniedHandlerImpl());

        // 设置授权异常页面
        final String accessDenyPage = clientProperties.getAccessDenyPage();
        if (StringUtils.hasText(accessDenyPage)) {
            http.exceptionHandling().accessDeniedPage(accessDenyPage);
        }
    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {

        final Map<UriHttpMethodTuple, Set<String>> accessMap = new HashMap<>(16);

        String accessExp = clientProperties.getAccessExp();
        if (clientProperties.getEnableRestfulApi()) {
            accessExp = clientProperties.getRestfulAccessExp();
        }

        try {
            // 判断是否有 @EnableGlobalMethodSecurity 注释
            this.applicationContext.getBean(GlobalMethodSecurityConfiguration.class);
            // 有 @EnableGlobalMethodSecurity 注释, 直接用 accessExp 表达式, restfulAccessExp 失效
            accessExp = clientProperties.getAccessExp();
        }
        catch (Exception e) {
            // 没有 @EnableGlobalMethodSecurity 注释, do nothing
        }

        // 这里 tuple(null, accessExp) 的唯一作用是作为 key 值, 无实际意义
        accessMap.put(UriHttpMethodTuple.tuple(null, accessExp), Collections.singleton(accessExp));
        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);
        resultMap.put(HttpSecurityAware.ACCESS, accessMap);

        final String accessDenyPage = clientProperties.getAccessDenyPage();
        if (StringUtils.hasText(accessDenyPage)) {
            final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(1);
            permitAllMap.put(tuple(GET, accessDenyPage), null);
            resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);
        }

        return resultMap;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
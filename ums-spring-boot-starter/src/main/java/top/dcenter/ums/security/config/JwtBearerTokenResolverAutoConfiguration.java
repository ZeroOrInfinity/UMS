/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.dcenter.ums.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.auth.config.SecurityAutoConfiguration;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.auth.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.ums.security.core.oauth.config.Auth2AutoConfiguration;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.jwt.config.JwtAutoConfiguration;
import top.dcenter.ums.security.jwt.resolver.UmsBearerTokenResolver;

import java.util.HashSet;
import java.util.Set;

/**
 * 针对 {@link UmsBearerTokenResolver} 的 ignoreUrls 的自动配置
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.8 12:56
 */
@Configuration
@Slf4j
@AutoConfigureAfter({Auth2AutoConfiguration.class, SecurityAutoConfiguration.class, JwtAutoConfiguration.class})
public class JwtBearerTokenResolverAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final Set<String> ignoreUrls = new HashSet<>();

    public JwtBearerTokenResolverAutoConfiguration(ClientProperties clientProperties,
                                                   Auth2Properties auth2Properties,
                                                   SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties) {
        ignoreUrls.add(clientProperties.getLoginPage());
        ignoreUrls.add(clientProperties.getLoginProcessingUrl());
        if (clientProperties.getOpenAuthenticationRedirect()) {
            ignoreUrls.add(clientProperties.getLoginUnAuthenticationRoutingUrl());
        }
        if (smsCodeLoginAuthenticationProperties.getSmsCodeLoginIsOpen()) {
            ignoreUrls.add(smsCodeLoginAuthenticationProperties.getLoginProcessingUrlMobile());
        }
        if (auth2Properties.getEnabled()) {
            ignoreUrls.add(auth2Properties.getAuthLoginUrlPrefix() + "/*");
            ignoreUrls.add(auth2Properties.getRedirectUrlPrefix() + "/*");
        }
    }

    @Override
    public void afterPropertiesSet() {
        try {
            UmsBearerTokenResolver umsBearerTokenResolver = this.applicationContext.getBean(UmsBearerTokenResolver.class);
            umsBearerTokenResolver.addIgnoreUrls(this.ignoreUrls);
        }
        catch (Exception e) {
            log.warn("JWT 功能未开启...");
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

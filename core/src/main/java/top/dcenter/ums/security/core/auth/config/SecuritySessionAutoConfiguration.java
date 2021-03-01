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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.session.SessionEnhanceCheckService;
import top.dcenter.ums.security.core.auth.controller.InvalidSessionController;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.auth.session.filter.SessionEnhanceCheckFilter;
import top.dcenter.ums.security.core.auth.session.strategy.DefaultForwardInvalidSessionStrategy;
import top.dcenter.ums.security.core.auth.session.strategy.DefaultRedirectInvalidSessionStrategy;
import top.dcenter.ums.security.core.auth.session.strategy.EnhanceConcurrentControlAuthenticationStrategy;

import static top.dcenter.ums.security.common.utils.AppContextUtil.registerDelegateApplicationListener;


/**
 * spring session 相关配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/28 21:44
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class, SecurityAutoConfiguration.class})
@Slf4j
public class SecuritySessionAutoConfiguration {

    private final ClientProperties clientProperties;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SessionEnhanceCheckService sessionEnhanceCheckService;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private FindByIndexNameSessionRepository<?> sessionRepository;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    public SecuritySessionAutoConfiguration(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }


    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.controller.InvalidSessionController")
    @ConditionalOnProperty(prefix = "ums.client.session", name = "forward-or-redirect", havingValue = "false")
    public InvalidSessionController invalidSessionController() {
        return new InvalidSessionController(clientProperties);
    }

    @Bean
    public SessionEnhanceCheckFilter sessionEnhanceCheckFilter(BaseAuthenticationFailureHandler baseAuthenticationFailureHandler) {

        return new SessionEnhanceCheckFilter(baseAuthenticationFailureHandler, sessionEnhanceCheckService);
    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.web.session.InvalidSessionStrategy")
    @ConditionalOnProperty(prefix = "ums.client.session", name = "forward-or-redirect", havingValue = "false")
    public InvalidSessionStrategy redirectInvalidSessionStrategy() {
        return new DefaultRedirectInvalidSessionStrategy(clientProperties.getSession().getInvalidSessionUrl());
    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.web.session.InvalidSessionStrategy")
    @ConditionalOnProperty(prefix = "ums.client.session", name = "forward-or-redirect", havingValue = "true")
    public InvalidSessionStrategy forwardInvalidSessionStrategy() {
        return new DefaultForwardInvalidSessionStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.session.strategy.EnhanceConcurrentControlAuthenticationStrategy")
    public EnhanceConcurrentControlAuthenticationStrategy enhanceConcurrentControlAuthenticationStrategy(SessionRegistry sessionRegistry) throws Exception {
        if (applicationContext == null) {
            throw new Exception("启动失败: " + EnhanceConcurrentControlAuthenticationStrategy.class.getName());
        }

        return new EnhanceConcurrentControlAuthenticationStrategy(sessionEnhanceCheckService, sessionRegistry, clientProperties);
    }

    /**
     * 根据 {@code org.springframework.boot.autoconfigure.session.StoreType} 类型(同时要添加相应Spring session的依赖), 生产不同的
     * {@link SessionRegistry}
     * @param applicationContext applicationContext
     * @return {@link SessionRegistry}
     */
    @Bean
    public SessionRegistry sessionRegistry(ApplicationContext applicationContext) {

        if (this.sessionRepository == null)
        {
            // 默认用单机 session 策略
            SessionRegistryImpl sessionRegistryImpl = new SessionRegistryImpl();
            registerDelegateApplicationListener(applicationContext, sessionRegistryImpl);
            return sessionRegistryImpl;
        } else
        {
            // 根据注入的不同 sessionRepository 创建的 session 集群策略
            return new SpringSessionBackedSessionRegistry<>(sessionRepository);
        }
    }

}
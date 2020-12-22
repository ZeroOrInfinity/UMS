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
package top.dcenter.ums.security.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.common.propertis.RememberMeProperties;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;

import java.util.UUID;

/**
 * 功能:<br>
 * 1. 注册 {@link ApplicationListener}到 {@link ApplicationContext}
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.17 15:24
 */
public final class AppContextUtil {

    private AppContextUtil() { }

    /**
     * 注册 {@link ApplicationListener} 到 {@link DelegatingApplicationListener}
     * @param applicationContext  {@link ApplicationContext}
     * @param delegate  {@link ApplicationListener}
     */
    public static void registerDelegateApplicationListener(ApplicationContext applicationContext,
                                                           ApplicationListener<?> delegate) {

        if (applicationContext.getBeansOfType(DelegatingApplicationListener.class).isEmpty()) {
            return;
        }
        DelegatingApplicationListener delegating = applicationContext.getBean(DelegatingApplicationListener.class);
        SmartApplicationListener smartListener = new GenericApplicationListenerAdapter(delegate);
        delegating.addListener(smartListener);
    }

    /**
     * 给 {@link AbstractAuthenticationProcessingFilter} 注册 {@link BaseAuthenticationFailureHandler},
     * {@link BaseAuthenticationSuccessHandler} 和 {@link PersistentTokenBasedRememberMeServices}.
     * @param abstractAuthenticationProcessingFilter    abstractAuthenticationProcessingFilter
     * @param baseAuthenticationSuccessHandler          认证成功处理器
     * @param baseAuthenticationFailureHandler          认证失败处理器
     * @param persistentTokenRepository                 RememberMe 持久化 Repository, 如果不为 null, rememberMe 也不为 null
     * @param userDetailsService                        本地用户服务
     * @param rememberMe                                rememberMe 属性, 如果不为 null, persistentTokenRepository 也不为 null
     */
    public static void registerHandlerAndRememberMeServices(AbstractAuthenticationProcessingFilter abstractAuthenticationProcessingFilter,
                                                            @Nullable BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                                            @Nullable BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                            @Nullable PersistentTokenRepository persistentTokenRepository,
                                                            UmsUserDetailsService userDetailsService,
                                                            @Nullable RememberMeServices rememberMeServices,
                                                            @Nullable RememberMeProperties rememberMe) {

        if (baseAuthenticationFailureHandler != null)
        {
            // 添加认证失败处理器
            abstractAuthenticationProcessingFilter.setAuthenticationFailureHandler(baseAuthenticationFailureHandler);
        }
        if (baseAuthenticationSuccessHandler != null)
        {
            // 添加认证成功处理器
            abstractAuthenticationProcessingFilter.setAuthenticationSuccessHandler(baseAuthenticationSuccessHandler);
        }

        if (rememberMeServices != null) {
            abstractAuthenticationProcessingFilter.setRememberMeServices(rememberMeServices);
        }
        else {
            // 添加 PersistentTokenBasedRememberMeServices, 不支持多租户
            if (persistentTokenRepository != null && rememberMe != null) {
                PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices =
                        new PersistentTokenBasedRememberMeServices(UUID.randomUUID().toString(), userDetailsService,
                                                                   persistentTokenRepository);

                persistentTokenBasedRememberMeServices.setTokenValiditySeconds(Integer.parseInt(String.valueOf(rememberMe.getRememberMeTimeout().getSeconds())));
                persistentTokenBasedRememberMeServices.setParameter(rememberMe.getRememberMeCookieName());
                // 添加rememberMe功能配置
                abstractAuthenticationProcessingFilter.setRememberMeServices(persistentTokenBasedRememberMeServices);
            }
        }
    }

}

package top.dcenter.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.SessionManagementFilter;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.api.service.CacheUserDetailsService;
import top.dcenter.security.core.api.session.SessionEnhanceCheckService;
import top.dcenter.security.core.auth.session.filter.SessionEnhanceCheckFilter;
import top.dcenter.security.core.auth.session.strategy.ClientExpiredSessionStrategy;
import top.dcenter.security.core.auth.session.strategy.DefaultRedirectInvalidSessionStrategy;
import top.dcenter.security.core.auth.session.strategy.EnhanceConcurrentControlAuthenticationStrategy;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * spring session 相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 14:06
 */
@Configuration
@AutoConfigureAfter(value = {SecuritySessionConfiguration.class, SecurityConfiguration.class})
public class SessionConfigurerAware implements WebSecurityConfigurerAware {

    private final ClientProperties clientProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SessionEnhanceCheckService sessionEnhanceCheckService;
    private final SessionEnhanceCheckFilter sessionEnhanceCheckFilter;
    private ObjectMapper objectMapper;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SessionRegistry sessionRegistry;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private CacheUserDetailsService cacheUserDetailsService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SessionConfigurerAware(ClientProperties clientProperties,
                                  BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                  SessionEnhanceCheckFilter sessionEnhanceCheckFilter, ObjectMapper objectMapper) {
        this.clientProperties = clientProperties;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.sessionEnhanceCheckFilter = sessionEnhanceCheckFilter;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {

        // 添加增强的 session 安全检测, SessionEnhanceCheckFilter 依赖 EnhanceChangeSessionIdAuthenticationStrategy
        http.addFilterAfter(sessionEnhanceCheckFilter, SessionManagementFilter.class);

        // 基本 session 配置
        http.sessionManagement()
                .sessionCreationPolicy(clientProperties.getSession().getSessionCreationPolicy())
                .sessionAuthenticationFailureHandler(baseAuthenticationFailureHandler)
                .sessionAuthenticationStrategy(new EnhanceConcurrentControlAuthenticationStrategy(sessionEnhanceCheckService, getSessionRegistry(http), clientProperties))
                .sessionAuthenticationErrorUrl(clientProperties.getLoginPage())
                .invalidSessionStrategy(new DefaultRedirectInvalidSessionStrategy(clientProperties.getSession().getInvalidSessionUrl()))
                .enableSessionUrlRewriting(clientProperties.getSession().getEnableSessionUrlRewriting());


        // 配置限制用户登录的 session 数量, 以及是否自动踢掉上一个登录成功的 session
        if (clientProperties.getSession().getSessionNumberControl())
        {
            http.sessionManagement()
                // 当设置为 1 时，同个用户登录会自动踢掉上一次的登录状态。
                .maximumSessions(clientProperties.getSession().getMaximumSessions())
                // 当为 true 时,同个用户达到最大 maximumSession 后，自动拒绝用户在登录
                .maxSessionsPreventsLogin(clientProperties.getSession().getMaxSessionsPreventsLogin())
                .expiredSessionStrategy(new ClientExpiredSessionStrategy(clientProperties, objectMapper));
        }

    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> permitSet = new HashSet<>();
        permitSet.add(clientProperties.getSession().getInvalidSessionUrl());
        permitSet.add(clientProperties.getSession().getInvalidSessionOfConcurrentUrl());

        Map<String, Set<String>> permitMap = new HashMap<>(16);
        permitMap.put(permitAll, permitSet);

        return permitMap;
    }


    private SessionRegistry getSessionRegistry(HttpSecurity http) {
        if (this.sessionRegistry == null) {
            SessionRegistryImpl sessionRegistryImpl = new SessionRegistryImpl();
            registerDelegateApplicationListener(http, sessionRegistryImpl);
            this.sessionRegistry = sessionRegistryImpl;
        }
        return this.sessionRegistry;
    }

    private void registerDelegateApplicationListener(HttpSecurity http,
                                                     ApplicationListener<?> delegate) {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        if (context == null) {
            return;
        }
        if (context.getBeansOfType(DelegatingApplicationListener.class).isEmpty()) {
            return;
        }
        DelegatingApplicationListener delegating = context
                .getBean(DelegatingApplicationListener.class);
        SmartApplicationListener smartListener = new GenericApplicationListenerAdapter(
                delegate);
        delegating.addListener(smartListener);
    }
}

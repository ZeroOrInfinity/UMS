package top.dcenter.ums.security.core.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.session.SessionEnhanceCheckService;
import top.dcenter.ums.security.core.api.session.strategy.DefaultRedirectInvalidSessionStrategy;
import top.dcenter.ums.security.core.api.session.strategy.EnhanceConcurrentControlAuthenticationStrategy;
import top.dcenter.ums.security.core.auth.controller.InvalidSessionController;
import top.dcenter.ums.security.core.auth.session.filter.SessionEnhanceCheckFilter;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;

import static top.dcenter.ums.security.core.util.MvcUtil.registerDelegateApplicationListener;

/**
 * spring session 相关配置
 * @author zyw
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
    public InvalidSessionController invalidSessionController(ObjectMapper objectMapper) {
        return new InvalidSessionController(clientProperties, objectMapper);
    }

    @Bean
    public SessionEnhanceCheckFilter sessionEnhanceCheckFilter(BaseAuthenticationFailureHandler baseAuthenticationFailureHandler) {

        return new SessionEnhanceCheckFilter(baseAuthenticationFailureHandler, sessionEnhanceCheckService);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.session.strategy.DefaultRedirectInvalidSessionStrategy")
    public DefaultRedirectInvalidSessionStrategy defaultRedirectInvalidSessionStrategy() {
        return new DefaultRedirectInvalidSessionStrategy(clientProperties.getSession().getInvalidSessionUrl());
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.session.strategy.EnhanceConcurrentControlAuthenticationStrategy")
    public EnhanceConcurrentControlAuthenticationStrategy enhanceConcurrentControlAuthenticationStrategy(SessionRegistry sessionRegistry) throws Exception {
        if (applicationContext == null) {
            log.error("启动失败: {}", EnhanceConcurrentControlAuthenticationStrategy.class.getName());
            throw new Exception("启动失败: " + EnhanceConcurrentControlAuthenticationStrategy.class.getName());
        }

        return new EnhanceConcurrentControlAuthenticationStrategy(sessionEnhanceCheckService, sessionRegistry, clientProperties);
    }

    /**
     * 根据 {@link org.springframework.boot.autoconfigure.session.StoreType} 类型(同时要添加相应Spring session的依赖), 生产不同的
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

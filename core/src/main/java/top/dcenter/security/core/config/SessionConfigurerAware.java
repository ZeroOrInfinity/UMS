package top.dcenter.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.session.SessionManagementFilter;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.config.HttpSecurityAware;
import top.dcenter.security.core.api.session.strategy.DefaultRedirectInvalidSessionStrategy;
import top.dcenter.security.core.api.session.strategy.EnhanceConcurrentControlAuthenticationStrategy;
import top.dcenter.security.core.auth.session.filter.SessionEnhanceCheckFilter;
import top.dcenter.security.core.auth.session.strategy.ClientExpiredSessionStrategy;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * spring session 相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 14:06
 */
@Configuration
@AutoConfigureAfter(value = {SecuritySessionConfiguration.class, SecurityConfiguration.class})
public class SessionConfigurerAware implements HttpSecurityAware {

    private final ClientProperties clientProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final SessionEnhanceCheckFilter sessionEnhanceCheckFilter;
    private final DefaultRedirectInvalidSessionStrategy defaultRedirectInvalidSessionStrategy;
    private final ObjectMapper objectMapper;

    private final EnhanceConcurrentControlAuthenticationStrategy enhanceConcurrentControlAuthenticationStrategy;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SessionConfigurerAware(ClientProperties clientProperties,
                                  BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                  SessionEnhanceCheckFilter sessionEnhanceCheckFilter,
                                  DefaultRedirectInvalidSessionStrategy defaultRedirectInvalidSessionStrategy,
                                  ObjectMapper objectMapper,
                                  EnhanceConcurrentControlAuthenticationStrategy enhanceConcurrentControlAuthenticationStrategy) throws Exception {
        this.clientProperties = clientProperties;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.sessionEnhanceCheckFilter = sessionEnhanceCheckFilter;
        this.defaultRedirectInvalidSessionStrategy = defaultRedirectInvalidSessionStrategy;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.enhanceConcurrentControlAuthenticationStrategy = enhanceConcurrentControlAuthenticationStrategy;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {

        // 添加增强的 session 安全检测, SessionEnhanceCheckFilter 依赖 EnhanceChangeSessionIdAuthenticationStrategy
        http.addFilterAfter(sessionEnhanceCheckFilter, SessionManagementFilter.class);

        // 基本 session 配置
        http.sessionManagement()
                .sessionCreationPolicy(clientProperties.getSession().getSessionCreationPolicy())
                .sessionAuthenticationFailureHandler(baseAuthenticationFailureHandler)
                .sessionAuthenticationStrategy(enhanceConcurrentControlAuthenticationStrategy)
                .sessionAuthenticationErrorUrl(clientProperties.getLoginPage())
                .invalidSessionStrategy(defaultRedirectInvalidSessionStrategy)
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
    public Map<String, Map<String, Set<String>>> getAuthorizeRequestMap() {

        final Map<String, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(clientProperties.getSession().getInvalidSessionUrl(), null);
        permitAllMap.put(clientProperties.getSession().getInvalidSessionOfConcurrentUrl(), null);

        Map<String, Map<String, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.permitAll, permitAllMap);

        return resultMap;
    }

}

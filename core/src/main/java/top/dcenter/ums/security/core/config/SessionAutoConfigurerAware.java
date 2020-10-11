package top.dcenter.ums.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.session.SessionManagementFilter;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.api.session.strategy.DefaultRedirectInvalidSessionStrategy;
import top.dcenter.ums.security.core.api.session.strategy.EnhanceConcurrentControlAuthenticationStrategy;
import top.dcenter.ums.security.core.auth.session.filter.SessionEnhanceCheckFilter;
import top.dcenter.ums.security.core.auth.session.strategy.ClientExpiredSessionStrategy;
import top.dcenter.ums.security.core.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static top.dcenter.ums.security.core.bean.UriHttpMethodTuple.tuple;

/**
 * spring session 相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 14:06
 */
@Configuration
@AutoConfigureAfter(value = {SecuritySessionAutoConfiguration.class, PropertiesAutoConfiguration.class})
public class SessionAutoConfigurerAware implements HttpSecurityAware {

    private final ClientProperties clientProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final SessionEnhanceCheckFilter sessionEnhanceCheckFilter;
    private final DefaultRedirectInvalidSessionStrategy defaultRedirectInvalidSessionStrategy;
    private final ObjectMapper objectMapper;

    private final EnhanceConcurrentControlAuthenticationStrategy enhanceConcurrentControlAuthenticationStrategy;

    public SessionAutoConfigurerAware(ClientProperties clientProperties,
                                      BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                      SessionEnhanceCheckFilter sessionEnhanceCheckFilter,
                                      DefaultRedirectInvalidSessionStrategy defaultRedirectInvalidSessionStrategy,
                                      ObjectMapper objectMapper,
                                      EnhanceConcurrentControlAuthenticationStrategy enhanceConcurrentControlAuthenticationStrategy) {
        this.clientProperties = clientProperties;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.sessionEnhanceCheckFilter = sessionEnhanceCheckFilter;
        this.defaultRedirectInvalidSessionStrategy = defaultRedirectInvalidSessionStrategy;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.enhanceConcurrentControlAuthenticationStrategy = enhanceConcurrentControlAuthenticationStrategy;
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
    public void postConfigure(HttpSecurity http) {
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
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {

        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(tuple(GET, clientProperties.getSession().getInvalidSessionUrl()), null);
        permitAllMap.put(tuple(GET, clientProperties.getSession().getInvalidSessionOfConcurrentUrl()), null);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }

}

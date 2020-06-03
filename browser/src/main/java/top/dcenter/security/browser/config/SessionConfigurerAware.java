package top.dcenter.security.browser.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;
import top.dcenter.security.browser.api.session.SessionEnhanceCheckService;
import top.dcenter.security.browser.session.filter.SessionEnhanceCheckFilter;
import top.dcenter.security.browser.session.strategy.BrowserExpiredSessionStrategy;
import top.dcenter.security.browser.session.strategy.EnhanceChangeSessionIdAuthenticationStrategy;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.config.SecurityConfiguration;
import top.dcenter.security.core.consts.SecurityConstants;
import top.dcenter.security.core.properties.BrowserProperties;

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

    private final BrowserProperties browserProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SessionEnhanceCheckService sessionEnhanceCheckService;
    private final SessionEnhanceCheckFilter sessionEnhanceCheckFilter;
    private ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SessionConfigurerAware(BrowserProperties browserProperties,
                                  BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                  SessionEnhanceCheckFilter sessionEnhanceCheckFilter, ObjectMapper objectMapper) {
        this.browserProperties = browserProperties;
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
        http.addFilterAfter(this.sessionEnhanceCheckFilter, SessionManagementFilter.class);

        // 基本 session 配置
        http.sessionManagement()
                .sessionCreationPolicy(this.browserProperties.getSession().getSessionCreationPolicy())
                .sessionAuthenticationFailureHandler(this.baseAuthenticationFailureHandler)
                .sessionAuthenticationStrategy(new EnhanceChangeSessionIdAuthenticationStrategy(this.sessionEnhanceCheckService))
                .sessionAuthenticationErrorUrl(this.browserProperties.getLoginPage())
                .invalidSessionStrategy(new SimpleRedirectInvalidSessionStrategy(this.browserProperties.getSession().getInvalidSessionUrl()))
                .enableSessionUrlRewriting(this.browserProperties.getSession().getEnableSessionUrlRewriting());


        // 配置限制用户登录的 session 数量, 以及是否自动踢掉上一个登录成功的 session
        if (this.browserProperties.getSession().getSessionNumberControl())
        {
            // TODO Session 各种 Strategy 未配置
            http.sessionManagement()
                // 当设置为 1 时，同个用户登录会自动踢掉上一次的登录状态。
                .maximumSessions(this.browserProperties.getSession().getMaximumSessions())
                // 同个用户达到最大 maximumSession 后，自动拒绝用户在登录
                .maxSessionsPreventsLogin(this.browserProperties.getSession().getMaxSessionsPreventsLogin())
                .expiredSessionStrategy(new BrowserExpiredSessionStrategy(browserProperties, objectMapper));
        }
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> permitSet = new HashSet<>();
        permitSet.add(SecurityConstants.DEFAULT_SESSION_INVALID_URL);

        Map<String, Set<String>> permitMap = new HashMap<>(16);
        permitMap.put(permitAll, permitSet);

        return permitMap;
    }
}

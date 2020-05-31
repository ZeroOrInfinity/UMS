package top.dcenter.security.browser.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware;
import top.dcenter.security.core.config.SecurityConfiguration;
import top.dcenter.security.core.properties.BrowserProperties;

import java.util.Map;
import java.util.Set;

/**
 * spring session 相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 14:06
 */
@Configuration
@AutoConfigureAfter(value = {SecuritySessionConfiguration.class, SecurityConfiguration.class})
public class SessionConfigurerAware implements SocialWebSecurityConfigurerAware {

    private final BrowserProperties browserProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SessionConfigurerAware(BrowserProperties browserProperties,
                                  BaseAuthenticationFailureHandler baseAuthenticationFailureHandler) {
        this.browserProperties = browserProperties;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(this.browserProperties.getSessionCreationPolicy())
                .sessionAuthenticationFailureHandler(this.baseAuthenticationFailureHandler)
                .sessionAuthenticationErrorUrl(this.browserProperties.getLoginPage())
                .enableSessionUrlRewriting(this.browserProperties.getEnableSessionUrlRewriting());

        // 配置 session 策略
        if (this.browserProperties.getSessionNumberSetting())
        {
            // TODO Session 各种 Strategy 未配置
            http.sessionManagement()
                // 当设置为 1 时，同个用户登录会自动踢掉上一次的登录状态。
                .maximumSessions(this.browserProperties.getMaximumSessions())
                // 同个用户达到最大 maximumSession 后，自动拒绝用户在登录
                .maxSessionsPreventsLogin(this.browserProperties.getMaxSessionsPreventsLogin())
                .expiredUrl(this.browserProperties.getLoginPage());
        }
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        return null;
    }
}

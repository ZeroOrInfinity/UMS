package top.dcenter.security.browser.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
@ConditionalOnClass(name = {"org.springframework.session.Session.class"})
@AutoConfigureAfter({SecurityConfiguration.class})
public class SessionConfigurerAware implements SocialWebSecurityConfigurerAware {

    private final BrowserProperties browserProperties;

    public SessionConfigurerAware(BrowserProperties browserProperties) {
        this.browserProperties = browserProperties;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 配置 session 策略
        if (browserProperties.getSessionNumberSetting())
        {
            // TODO Session 各种 Strategy 未配置
            http.sessionManagement()
                .sessionAuthenticationErrorUrl(browserProperties.getLoginPage())
                // 当设置为 1 时，同个用户登录会自动踢掉上一次的登录状态。
                .maximumSessions(browserProperties.getMaximumSessions())
                // 同个用户达到最大 maximumSession 后，自动拒绝用户在登录
                .maxSessionsPreventsLogin(browserProperties.getMaxSessionsPreventsLogin())
                .expiredUrl(browserProperties.getLoginPage());
        }
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        return null;
    }
}

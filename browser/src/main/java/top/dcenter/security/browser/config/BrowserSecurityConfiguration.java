package top.dcenter.security.browser.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.security.browser.authentication.handler.BrowserAuthenticationFailureHandler;
import top.dcenter.security.browser.authentication.handler.BrowserAuthenticationSuccessHandler;
import top.dcenter.security.browser.controller.BrowserSecurityController;
import top.dcenter.security.browser.rememberme.repository.JdbcTokenRepositoryFactory;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.api.repository.UserTokenRepositoryFactory;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.sql.DataSource;

/**
 * 网页端安全相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 21:44
 */
@Configuration
@Slf4j
public class BrowserSecurityConfiguration {

    private final BrowserProperties browserProperties;
    private final DataSource dataSource;

    public BrowserSecurityConfiguration(BrowserProperties browserProperties, DataSource dataSource) {
        this.browserProperties = browserProperties;
        this.dataSource = dataSource;
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.browser.api.controller.BaseBrowserSecurityController")
    public BrowserSecurityController browserSecurityController() {
        return new BrowserSecurityController(this.browserProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler")
    public BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler(ObjectMapper objectMapper,
                                                                             BrowserProperties browserProperties) {
        return new BrowserAuthenticationSuccessHandler(objectMapper, browserProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler")
    public BaseAuthenticationFailureHandler baseAuthenticationFailureHandler(ObjectMapper objectMapper,
                                                                             BrowserProperties browserProperties) {
        return new BrowserAuthenticationFailureHandler(objectMapper, browserProperties);
    }

    /**
     * 与 spring Security RememberMe 功能相关, 如果使用了 spring session 功能，就不需要 RememberMe 相关功能了
     * @return  {@link UserTokenRepositoryFactory}
     */
    @Bean
    @ConditionalOnMissingClass({"org.springframework.session.Session"})
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.api.repository.UserTokenRepositoryFactory")
    public UserTokenRepositoryFactory userTokenRepositoryFactory() {
        return new JdbcTokenRepositoryFactory(this.dataSource);
    }

    /**
     * 与 spring Security RememberMe 功能相关, 如果使用了 spring session 功能，就不需要 RememberMe 相关功能了
     * @return {@link PersistentTokenRepository}
     */
    @Bean
    @ConditionalOnMissingClass({"org.springframework.session.Session"})
    public PersistentTokenRepository persistentTokenRepository() {
        return userTokenRepositoryFactory().getUserTokenRepository();
    }
}

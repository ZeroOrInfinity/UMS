package top.dcenter.security.browser.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.dcenter.security.browser.api.session.SessionEnhanceCheckService;
import top.dcenter.security.browser.session.filter.SessionEnhanceCheckFilter;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.config.PropertiesConfiguration;
import top.dcenter.security.core.config.SecurityConfiguration;

/**
 * spring session 相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 21:44
 */
@Configuration
@AutoConfigureAfter({PropertiesConfiguration.class, SecurityConfiguration.class})
@Slf4j
public class SecuritySessionConfiguration {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SessionEnhanceCheckService sessionEnhanceCheckService;

    @Bean
    public SessionEnhanceCheckFilter sessionEnhanceCheckFilter(BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                               ServletWebServerApplicationContext servletWebServerApplicationContext) {

        return new SessionEnhanceCheckFilter(baseAuthenticationFailureHandler, this.sessionEnhanceCheckService, servletWebServerApplicationContext);
    }

}

package top.dcenter.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.dcenter.security.core.api.advice.SecurityControllerExceptionHandler;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.authentication.handler.BrowserAuthenticationFailureHandler;
import top.dcenter.security.core.authentication.handler.BrowserAuthenticationSuccessHandler;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.sql.DataSource;

/**
 * security 配置
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:59
 */
@Configuration
@AutoConfigureAfter({PropertiesConfiguration.class})
public class SecurityConfiguration {

    private final BrowserProperties browserProperties;
    private final DataSource dataSource;
    protected final ObjectMapper objectMapper;

    public SecurityConfiguration(BrowserProperties browserProperties, DataSource dataSource, ObjectMapper objectMapper) {
        this.browserProperties = browserProperties;
        this.dataSource = dataSource;
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.api.advice.SecurityControllerExceptionHandler")
    public SecurityControllerExceptionHandler securityControllerExceptionHandler() {
        return new SecurityControllerExceptionHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 的实现了添加随机 salt 算法，并且能从hash后的字符串中获取 salt 进行原始密码与hash后的密码的对比
        return new BCryptPasswordEncoder();
    }

}

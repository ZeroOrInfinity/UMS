package top.dcenter.ums.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.dcenter.ums.security.core.api.advice.SecurityControllerExceptionHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.controller.BaseSecurityController;
import top.dcenter.ums.security.core.api.logout.DefaultLogoutSuccessHandler;
import top.dcenter.ums.security.core.api.service.AbstractUserDetailsService;
import top.dcenter.ums.security.core.auth.controller.ClientSecurityController;
import top.dcenter.ums.security.core.auth.handler.ClientAuthenticationFailureHandler;
import top.dcenter.ums.security.core.auth.handler.ClientAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.auth.provider.UsernamePasswordAuthenticationProvider;
import top.dcenter.ums.security.core.properties.ClientProperties;
import top.dcenter.ums.security.core.util.MvcUtil;

/**
 * security 配置
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 19:59
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class SecurityAutoConfiguration implements InitializingBean {

    private final ClientProperties clientProperties;
    private final ObjectMapper objectMapper;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired
    private AbstractUserDetailsService abstractUserDetailsService;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    public SecurityAutoConfiguration(ClientProperties clientProperties, ObjectMapper objectMapper) {
        this.clientProperties = clientProperties;
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 的实现了添加随机 salt 算法，并且能从hash后的字符串中获取 salt 进行原始密码与hash后的密码的对比
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler")
    public BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler() {
        return new ClientAuthenticationSuccessHandler(objectMapper, clientProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler")
    public BaseAuthenticationFailureHandler baseAuthenticationFailureHandler() {
        return new ClientAuthenticationFailureHandler(objectMapper, clientProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.advice.SecurityControllerExceptionHandler")
    public SecurityControllerExceptionHandler securityControllerExceptionHandler() {
        return new SecurityControllerExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.provider.UsernamePasswordAuthenticationProvider")
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider(PasswordEncoder passwordEncoder) {
        return new UsernamePasswordAuthenticationProvider(passwordEncoder, abstractUserDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.logout.DefaultLogoutSuccessHandler")
    public DefaultLogoutSuccessHandler defaultLogoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler(clientProperties, objectMapper);
    }


    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.controller.BaseSecurityController")
    @ConditionalOnProperty(prefix = "security.client", name = "open-authentication-redirect", havingValue = "true")
    public ClientSecurityController clientSecurityController() {
        return new ClientSecurityController(this.clientProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (clientProperties.getOpenAuthenticationRedirect())
        {
            // 在 mvc 中做 Uri 映射等动作
            MvcUtil.registerController("clientSecurityController", applicationContext, BaseSecurityController.class);
        }

    }
}

package top.dcenter.ums.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.support.GenericWebApplicationContext;
import top.dcenter.ums.security.core.api.advice.SecurityControllerExceptionHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.logout.DefaultLogoutSuccessHandler;
import top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.controller.ClientSecurityController;
import top.dcenter.ums.security.core.auth.handler.ClientAuthenticationFailureHandler;
import top.dcenter.ums.security.core.auth.handler.ClientAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.auth.provider.UsernamePasswordAuthenticationProvider;
import top.dcenter.ums.security.core.permission.evaluator.UriAuthoritiesPermissionEvaluator;
import top.dcenter.ums.security.core.permission.listener.UpdateRolesAuthoritiesListener;
import top.dcenter.ums.security.core.permission.service.DefaultUriAuthorizeService;
import top.dcenter.ums.security.core.properties.ClientProperties;
import top.dcenter.ums.security.core.util.MvcUtil;

import java.lang.reflect.Field;
import java.util.Objects;

import static top.dcenter.ums.security.core.consts.SecurityConstants.SERVLET_CONTEXT_PATH_PARAM_NAME;

/**
 * security 配置
 * @author  zyw
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 19:59
 */
@Configuration(proxyBeanMethods = false)
@EnableAsync
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class SecurityAutoConfiguration implements InitializingBean {

    private final ClientProperties clientProperties;
    private final ObjectMapper objectMapper;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private UmsUserDetailsService umsUserDetailsService;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    public SecurityAutoConfiguration(ClientProperties clientProperties, ObjectMapper objectMapper) {
        this.clientProperties = clientProperties;
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public UpdateRolesAuthoritiesListener updateRolesAuthoritiesListener(UriAuthorizeService uriAuthorizeService) {
        return new UpdateRolesAuthoritiesListener(uriAuthorizeService);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService")
    public UriAuthorizeService uriAuthorizeService() {
        return new DefaultUriAuthorizeService();
    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.access.PermissionEvaluator")
    public UriAuthoritiesPermissionEvaluator uriAuthoritiesPermissionEvaluator(UriAuthorizeService uriAuthorizeService) {
        return new UriAuthoritiesPermissionEvaluator(uriAuthorizeService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 默认为 BCryptPasswordEncoder 的实现了添加随机 salt 算法，并且能从hash后的字符串中获取 salt 进行原始密码与hash后的密码的对比
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
        return new UsernamePasswordAuthenticationProvider(passwordEncoder, umsUserDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.logout.DefaultLogoutSuccessHandler")
    public DefaultLogoutSuccessHandler defaultLogoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler(clientProperties, objectMapper);
    }


    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.controller.BaseSecurityController")
    @ConditionalOnProperty(prefix = "ums.client", name = "open-authentication-redirect", havingValue = "true")
    public ClientSecurityController clientSecurityController() {
        return new ClientSecurityController(this.clientProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 给 MvcUtil.SERVLET_CONTEXT_PATH 设置 servletContextPath
        Class<MvcUtil> mvcUtilClass = MvcUtil.class;
        Class.forName(mvcUtilClass.getName());
        Field[] declaredFields = mvcUtilClass.getDeclaredFields();
        for (Field field : declaredFields)
        {
            field.setAccessible(true);
            if (Objects.equals(field.getName(), SERVLET_CONTEXT_PATH_PARAM_NAME))
            {
                String contextPath;
                try {
                    contextPath = Objects.requireNonNull(((AnnotationConfigServletWebServerApplicationContext) this.applicationContext).getServletContext()).getContextPath();
                }
                catch (Exception e) {
                    contextPath = Objects.requireNonNull(((GenericWebApplicationContext) this.applicationContext).getServletContext()).getContextPath();
                }
                field.set(null, contextPath);
            }

        }


    }
}

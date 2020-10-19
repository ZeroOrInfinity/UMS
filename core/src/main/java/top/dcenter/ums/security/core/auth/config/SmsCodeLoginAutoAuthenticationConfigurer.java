package top.dcenter.ums.security.core.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.mobile.SmsCodeLoginAuthenticationFilter;
import top.dcenter.ums.security.core.auth.mobile.SmsCodeLoginAuthenticationProvider;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.auth.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.registerHandlerAndRememberMeServices;


/**
 * 短信登录配置
 * @author  zyw
 * @version V1.0  Created by 2020/5/7 23:32
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.mobile.login", name = "sms-code-login-is-open", havingValue = "true")
@AutoConfigureAfter({SecurityAutoConfiguration.class})
public class SmsCodeLoginAutoAuthenticationConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ValidateCodeProperties validateCodeProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler;
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private UmsUserDetailsService userDetailsService;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private PersistentTokenRepository persistentTokenRepository;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private ClientProperties clientProperties;
    private final SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties;

    public SmsCodeLoginAutoAuthenticationConfigurer(ValidateCodeProperties validateCodeProperties,
                                                    BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                    BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                                    SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties) {
        this.validateCodeProperties = validateCodeProperties;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.smsCodeLoginAuthenticationProperties = smsCodeLoginAuthenticationProperties;
    }

    @Override
    public void configure(HttpSecurity http) {

        SmsCodeLoginAuthenticationFilter smsCodeLoginAuthenticationFilter =
                new SmsCodeLoginAuthenticationFilter(validateCodeProperties, smsCodeLoginAuthenticationProperties);
        smsCodeLoginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        registerHandlerAndRememberMeServices(smsCodeLoginAuthenticationFilter,
                                             baseAuthenticationSuccessHandler,
                                             baseAuthenticationFailureHandler,
                                             persistentTokenRepository,
                                             userDetailsService,
                                             clientProperties);

        SmsCodeLoginAuthenticationProvider smsCodeLoginAuthenticationProvider = new SmsCodeLoginAuthenticationProvider(userDetailsService);
        http.authenticationProvider(postProcess(smsCodeLoginAuthenticationProvider))
            .addFilterAfter(postProcess(smsCodeLoginAuthenticationFilter), AbstractPreAuthenticatedProcessingFilter.class);

    }

}

package top.dcenter.security.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.security.core.api.service.AbstractUserDetailsService;
import top.dcenter.security.core.authentication.mobile.SmsCodeAuthenticationFilter;
import top.dcenter.security.core.authentication.mobile.SmsCodeAuthenticationProvider;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.properties.ValidateCodeProperties;

import java.util.UUID;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;


/**
 * 短信登录配置
 * @author  zyw
 * @version V1.0  Created by 2020/5/7 23:32
 */
@Configuration
@ConditionalOnProperty(prefix = "security.smsCodeLogin", name = "sms-code-login-is-open", havingValue = "true")
public class SmsCodeAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ValidateCodeProperties validateCodeProperties;
    private final AuthenticationFailureHandler browserAuthenticationFailureHandler;
    private final AuthenticationSuccessHandler browserAuthenticationSuccessHandler;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private AbstractUserDetailsService userDetailsService;
    private String key;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private BrowserProperties browserProperties;

    public SmsCodeAuthenticationConfig(ValidateCodeProperties validateCodeProperties,
                                       AuthenticationFailureHandler browserAuthenticationFailureHandler,
                                       AuthenticationSuccessHandler browserAuthenticationSuccessHandler) {
        this.validateCodeProperties = validateCodeProperties;
        this.browserAuthenticationFailureHandler = browserAuthenticationFailureHandler;
        this.browserAuthenticationSuccessHandler = browserAuthenticationSuccessHandler;

    }

    @Override
    public void configure(HttpSecurity http) {

        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter =
                new SmsCodeAuthenticationFilter(this.validateCodeProperties);
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(this.browserAuthenticationSuccessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(this.browserAuthenticationFailureHandler);

        PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices =
                new PersistentTokenBasedRememberMeServices(getKey(), this.userDetailsService,
                                                           this.persistentTokenRepository);
        persistentTokenBasedRememberMeServices.setTokenValiditySeconds(this.browserProperties.getRememberMeSeconds());
        persistentTokenBasedRememberMeServices.setParameter(DEFAULT_REMEMBER_ME_NAME);
        // 添加rememberMe功能配置
        smsCodeAuthenticationFilter.setRememberMeServices(persistentTokenBasedRememberMeServices);


        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider(this.userDetailsService);
        http.authenticationProvider(smsCodeAuthenticationProvider)
            .addFilterAfter(smsCodeAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);


    }

    /**
     * Gets the key to use for validating remember me tokens. Either the value passed into
     * {@link #key(String)}, or a secure random string if none was specified.
     *
     * @return the remember me key to use
     */
    private String getKey() {
        if (this.key == null) {
            this.key = UUID.randomUUID().toString();
        }
        return this.key;
    }

    /**
     * Sets the key to identify tokens created for remember me authentication. Default is
     * a secure randomly generated key.
     *
     * @param key the key to identify tokens created for remember me authentication
     */
    public void key(String key) {
        this.key = key;
    }
}

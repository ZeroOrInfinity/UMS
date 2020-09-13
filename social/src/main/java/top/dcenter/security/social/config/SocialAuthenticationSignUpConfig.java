package top.dcenter.security.social.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.properties.ClientProperties;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailsService;
import top.dcenter.security.social.handler.SocialAuthenticationFailureHandler;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.signup.SocialAuthenticationSignUpFilter;
import top.dcenter.security.social.signup.SocialAuthenticationSignUpProvider;

import java.util.UUID;


/**
 * social 第三方授权登录注册配置
 * @author  zyw
 * @version V1.0  Created by 2020/5/7 23:32
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social", name = "social-sign-in-is-open", havingValue = "true")
public class SocialAuthenticationSignUpConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ProviderSignInUtils providerSignInUtils;
    private final BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private AbstractSocialUserDetailsService userDetailsService;
    private String key;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private PersistentTokenRepository persistentTokenRepository;
    private final ObjectMapper objectMapper;
    private final ClientProperties clientProperties;
    private final SocialProperties socialProperties;
    private final PasswordEncoder passwordEncoder;


    public SocialAuthenticationSignUpConfig(ProviderSignInUtils providerSignInUtils,
                                            BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                            ObjectMapper objectMapper,
                                            SocialProperties socialProperties,
                                            ClientProperties clientProperties,
                                            PasswordEncoder passwordEncoder) {
        this.providerSignInUtils = providerSignInUtils;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.clientProperties = clientProperties;
        this.socialProperties = socialProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(HttpSecurity http) {

        SocialAuthenticationSignUpFilter socialAuthenticationSignUpFilter = new SocialAuthenticationSignUpFilter(socialProperties);
        socialAuthenticationSignUpFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        baseAuthenticationSuccessHandler.getLoginUrls().add(socialProperties.getSignInUrl());
        baseAuthenticationSuccessHandler.getLoginUrls().add(socialProperties.getSignUpUrl());

        socialAuthenticationSignUpFilter.setAuthenticationSuccessHandler(baseAuthenticationSuccessHandler);
        SocialAuthenticationFailureHandler socialAuthenticationFailureHandler = new SocialAuthenticationFailureHandler(objectMapper,
                                                                                                                       socialProperties, clientProperties);
        socialAuthenticationSignUpFilter.setAuthenticationFailureHandler(socialAuthenticationFailureHandler);

        if (persistentTokenRepository != null)
        {
            PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices = new PersistentTokenBasedRememberMeServices(getKey(), userDetailsService, persistentTokenRepository);
            // 添加rememberMe功能配置
            socialAuthenticationSignUpFilter.setRememberMeServices(persistentTokenBasedRememberMeServices);
        }

        SocialAuthenticationSignUpProvider socialAuthenticationSignUpProvider =
                new SocialAuthenticationSignUpProvider(userDetailsService, providerSignInUtils, passwordEncoder);
        http.authenticationProvider(socialAuthenticationSignUpProvider)
            .addFilterAfter(socialAuthenticationSignUpFilter, AbstractPreAuthenticatedProcessingFilter.class);

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
     * Sets the key to identify tokens created for remember me auth. Default is
     * a secure randomly generated key.
     *
     * @param key the key to identify tokens created for remember me auth
     */
    public void key(String key) {
        this.key = key;
    }
}
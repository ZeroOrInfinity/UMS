package top.dcenter.security.social.authtication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailService;
import top.dcenter.security.social.SocialProperties;
import top.dcenter.security.social.handler.SocialAuthenticationFailureHandler;

import java.util.UUID;


/**
 * social 第三方授权登录注册配置
 * @author  zyw
 * @version V1.0  Created by 2020/5/7 23:32
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social", name = "social-sign-in-is-open", havingValue = "true")
public class SocialAuthenticationSignUpConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ProviderSignInUtils providerSignInUtils;
    private final AuthenticationSuccessHandler browserAuthenticationSuccessHandler;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private AbstractSocialUserDetailService userDetailsService;
    private String key;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    private final ObjectMapper objectMapper;
    private final BrowserProperties browserProperties;
    private final SocialProperties socialProperties;


    public SocialAuthenticationSignUpConfiguration(ProviderSignInUtils providerSignInUtils,
                                                   AuthenticationSuccessHandler browserAuthenticationSuccessHandler,
                                                   ObjectMapper objectMapper,
                                                   SocialProperties socialProperties,
                                                   BrowserProperties browserProperties) {
        this.providerSignInUtils = providerSignInUtils;
        this.browserAuthenticationSuccessHandler = browserAuthenticationSuccessHandler;
        this.objectMapper = objectMapper;
        this.browserProperties = browserProperties;
        this.socialProperties = socialProperties;
    }

    @Override
    public void configure(HttpSecurity http) {

        SocialAuthenticationSignUpFilter socialAuthenticationSignUpFilter = new SocialAuthenticationSignUpFilter();
        socialAuthenticationSignUpFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        socialAuthenticationSignUpFilter.setAuthenticationSuccessHandler(browserAuthenticationSuccessHandler);
        SocialAuthenticationFailureHandler socialAuthenticationFailureHandler = new SocialAuthenticationFailureHandler(this.objectMapper,
                                                                                                                       this.socialProperties,this.browserProperties);
        socialAuthenticationSignUpFilter.setAuthenticationFailureHandler(socialAuthenticationFailureHandler);

        PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices = new PersistentTokenBasedRememberMeServices(getKey(), userDetailsService, persistentTokenRepository);
        // 添加rememberMe功能配置
        socialAuthenticationSignUpFilter.setRememberMeServices(persistentTokenBasedRememberMeServices);

        SocialAuthenticationSignUpProvider socialAuthenticationSignUpProvider =
                new SocialAuthenticationSignUpProvider(userDetailsService, providerSignInUtils);
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
     * Sets the key to identify tokens created for remember me authentication. Default is
     * a secure randomly generated key.
     *
     * @param key the key to identify tokens created for remember me authentication
     */
    public void key(String key) {
        this.key = key;
    }
}

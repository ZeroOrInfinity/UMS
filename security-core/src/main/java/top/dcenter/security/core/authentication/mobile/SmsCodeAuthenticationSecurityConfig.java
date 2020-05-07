package top.dcenter.security.core.authentication.mobile;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.validate.code.ValidateCodeProperties;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/7 23:32
 */
@Component
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ValidateCodeProperties validateCodeProperties;
    private final AuthenticationFailureHandler browserAuthenticationFailureHandler;
    private final AuthenticationSuccessHandler browserAuthenticationSuccessHandler;
    private final UserDetailsService userDetailsService;
    public SmsCodeAuthenticationSecurityConfig(ValidateCodeProperties validateCodeProperties, AuthenticationFailureHandler browserAuthenticationFailureHandler, AuthenticationSuccessHandler browserAuthenticationSuccessHandler, UserDetailsService userDetailsService) {
        this.validateCodeProperties = validateCodeProperties;
        this.browserAuthenticationFailureHandler = browserAuthenticationFailureHandler;
        this.browserAuthenticationSuccessHandler = browserAuthenticationSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter(validateCodeProperties);
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(browserAuthenticationSuccessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(browserAuthenticationFailureHandler);

        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider(userDetailsService);

        http.authenticationProvider(smsCodeAuthenticationProvider)
            .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

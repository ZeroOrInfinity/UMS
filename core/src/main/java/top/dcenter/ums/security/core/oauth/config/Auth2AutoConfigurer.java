package top.dcenter.ums.security.core.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.oauth.filter.login.Auth2LoginAuthenticationFilter;
import top.dcenter.ums.security.core.oauth.filter.redirect.Auth2DefaultRequestRedirectFilter;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.provider.Auth2LoginAuthenticationProvider;
import top.dcenter.ums.security.core.oauth.repository.UsersConnectionRepository;
import top.dcenter.ums.security.core.oauth.service.Auth2UserService;
import top.dcenter.ums.security.core.oauth.signup.ConnectionService;

import java.util.concurrent.ExecutorService;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.registerHandlerAndRememberMeServices;

/**
 * 添加 OAuth2(JustAuth) 配置
 * @author zyw
 * @version V2.0  Created by 2020/10/12 12:31
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.oauth", name = "enabled", havingValue = "true")
@AutoConfigureAfter({Auth2AutoConfiguration.class})
public class Auth2AutoConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final Auth2Properties auth2Properties;
    private final UmsUserDetailsService umsUserDetailsService;
    private final Auth2UserService auth2UserService;
    private final UsersConnectionRepository usersConnectionRepository;
    private final ConnectionService connectionSignUp;
    private final ExecutorService updateConnectionTaskExecutor;
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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public Auth2AutoConfigurer(Auth2Properties auth2Properties, UmsUserDetailsService umsUserDetailsService,
                               Auth2UserService auth2UserService, UsersConnectionRepository usersConnectionRepository,
                               ConnectionService connectionSignUp,
                               @Qualifier("updateConnectionTaskExecutor") ExecutorService updateConnectionTaskExecutor, BaseAuthenticationFailureHandler baseAuthenticationFailureHandler, BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler) {
        this.auth2Properties = auth2Properties;
        this.umsUserDetailsService = umsUserDetailsService;
        this.auth2UserService = auth2UserService;
        this.usersConnectionRepository = usersConnectionRepository;
        this.connectionSignUp = connectionSignUp;
        this.updateConnectionTaskExecutor = updateConnectionTaskExecutor;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
    }

    @Override
    public void configure(HttpSecurity http) {

        // 添加第三方登录入口过滤器
        String authorizationRequestBaseUri = auth2Properties.getAuthLoginUrlPrefix();
        Auth2DefaultRequestRedirectFilter auth2DefaultRequestRedirectFilter = new Auth2DefaultRequestRedirectFilter(authorizationRequestBaseUri);
        http.addFilterAfter(postProcess(auth2DefaultRequestRedirectFilter), AbstractPreAuthenticatedProcessingFilter.class);

        // 添加第三方登录回调接口过滤器
        String filterProcessesUrl = auth2Properties.getRedirectUrlPrefix();
        Auth2LoginAuthenticationFilter auth2LoginAuthenticationFilter = new Auth2LoginAuthenticationFilter(filterProcessesUrl);
        AuthenticationManager sharedObject = http.getSharedObject(AuthenticationManager.class);
        auth2LoginAuthenticationFilter.setAuthenticationManager(sharedObject);
        registerHandlerAndRememberMeServices(auth2LoginAuthenticationFilter,
                                             baseAuthenticationSuccessHandler,
                                             baseAuthenticationFailureHandler,
                                             persistentTokenRepository,
                                             userDetailsService,
                                             clientProperties);
        http.addFilterAfter(postProcess(auth2LoginAuthenticationFilter), OAuth2AuthorizationRequestRedirectFilter.class);

        // 添加 provider
        Auth2LoginAuthenticationProvider auth2LoginAuthenticationProvider = new Auth2LoginAuthenticationProvider(
                auth2UserService, connectionSignUp, umsUserDetailsService,
                usersConnectionRepository, updateConnectionTaskExecutor);
        http.authenticationProvider(postProcess(auth2LoginAuthenticationProvider));
    }

}


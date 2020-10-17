package top.dcenter.ums.security.core.oauth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.oauth.filter.login.Auth2LoginAuthenticationFilter;
import top.dcenter.ums.security.core.oauth.filter.redirect.Auth2DefaultRequestRedirectFilter;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.provider.Auth2LoginAuthenticationProvider;
import top.dcenter.ums.security.core.oauth.repository.UsersConnectionRepository;
import top.dcenter.ums.security.core.oauth.service.Auth2UserService;
import top.dcenter.ums.security.core.oauth.signup.ConnectionService;

import java.util.concurrent.ExecutorService;

/**
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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public Auth2AutoConfigurer(Auth2Properties auth2Properties, UmsUserDetailsService umsUserDetailsService,
                               Auth2UserService auth2UserService, UsersConnectionRepository usersConnectionRepository,
                               ConnectionService connectionSignUp,
                               @Qualifier("updateConnectionTaskExecutor") ExecutorService updateConnectionTaskExecutor) {
        this.auth2Properties = auth2Properties;
        this.umsUserDetailsService = umsUserDetailsService;
        this.auth2UserService = auth2UserService;
        this.usersConnectionRepository = usersConnectionRepository;
        this.connectionSignUp = connectionSignUp;
        this.updateConnectionTaskExecutor = updateConnectionTaskExecutor;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        // 添加第三方登录入口过滤器
        String authorizationRequestBaseUri = auth2Properties.getAuthLoginUrlPrefix();
        Auth2DefaultRequestRedirectFilter auth2DefaultRequestRedirectFilter = new Auth2DefaultRequestRedirectFilter(authorizationRequestBaseUri);
        http.addFilterAfter(auth2DefaultRequestRedirectFilter, AbstractPreAuthenticatedProcessingFilter.class);

        // 添加 provider
        Auth2LoginAuthenticationProvider auth2LoginAuthenticationProvider = new Auth2LoginAuthenticationProvider(
                auth2UserService, connectionSignUp, umsUserDetailsService,
                usersConnectionRepository, updateConnectionTaskExecutor);
        http.authenticationProvider(auth2LoginAuthenticationProvider);

        // 添加第三方登录回调接口过滤器
        String filterProcessesUrl = auth2Properties.getRedirectUrlPrefix();
        Auth2LoginAuthenticationFilter auth2LoginAuthenticationFilter = new Auth2LoginAuthenticationFilter(filterProcessesUrl);
        AuthenticationManager sharedObject = http.getSharedObject(AuthenticationManager.class);
        auth2LoginAuthenticationFilter.setAuthenticationManager(sharedObject);
        http.addFilterAfter(auth2LoginAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class);

        http.authorizeRequests().antMatchers(HttpMethod.GET,
                                             auth2Properties.getRedirectUrlPrefix() + "/*",
                                             auth2Properties.getAuthLoginUrlPrefix() + "/*")
                                .permitAll();

    }

}


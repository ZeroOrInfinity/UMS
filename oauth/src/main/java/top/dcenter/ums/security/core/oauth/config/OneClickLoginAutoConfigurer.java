package top.dcenter.ums.security.core.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.oauth.oneclicklogin.service.OneClickLoginService;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.oauth.oneclicklogin.OneClickLoginAuthenticationFilter;
import top.dcenter.ums.security.core.oauth.oneclicklogin.OneClickLoginAuthenticationProvider;
import top.dcenter.ums.security.core.oauth.properties.OneClickLoginProperties;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;

import javax.servlet.http.HttpServletRequest;

/**
 * 一键登录自动配置
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.5.13 17:08
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.one-click-login", name = "enable", havingValue = "true")
@AutoConfigureAfter({SecurityAutoConfiguration.class, Auth2PropertiesAutoConfiguration.class})
public class OneClickLoginAutoConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final OneClickLoginProperties oneClickLoginProperties;
    private final OneClickLoginService oneClickLoginService;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler;
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private UmsUserDetailsService userDetailsService;
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private GenerateClaimsSetService generateClaimsSetService;

    public OneClickLoginAutoConfigurer(OneClickLoginProperties oneClickLoginProperties,
                                       OneClickLoginService oneClickLoginService,
                                       BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                       BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler) {
        this.oneClickLoginProperties = oneClickLoginProperties;
        this.oneClickLoginService = oneClickLoginService;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
    }

    @Override
    public void configure(HttpSecurity http) {

        OneClickLoginAuthenticationFilter oneClickLoginAuthenticationFilter =
                new OneClickLoginAuthenticationFilter(oneClickLoginService, oneClickLoginProperties,
                                                      authenticationDetailsSource);
        oneClickLoginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        oneClickLoginAuthenticationFilter.setAuthenticationSuccessHandler(baseAuthenticationSuccessHandler);
        oneClickLoginAuthenticationFilter.setAuthenticationFailureHandler(baseAuthenticationFailureHandler);
        OneClickLoginAuthenticationProvider oneClickLoginAuthenticationProvider =
                new OneClickLoginAuthenticationProvider(userDetailsService, oneClickLoginService,
                                                        generateClaimsSetService);
        http.authenticationProvider(postProcess(oneClickLoginAuthenticationProvider))
            .addFilterAfter(postProcess(oneClickLoginAuthenticationFilter), AbstractPreAuthenticatedProcessingFilter.class);

    }
}

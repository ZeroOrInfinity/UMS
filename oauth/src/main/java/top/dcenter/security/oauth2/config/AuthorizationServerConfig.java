package top.dcenter.security.oauth2.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import top.dcenter.security.core.api.config.HttpSecurityAware;
import top.dcenter.security.core.config.SecurityConfiguration;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/22 14:49
 */
@Configuration
@AutoConfigureAfter({
        SecurityConfiguration.class})
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter implements HttpSecurityAware, InitializingBean {

    private final ClientProperties clientProperties;
    private final PasswordEncoder passwordEncoder;

    public AuthorizationServerConfig(ClientProperties clientProperties, PasswordEncoder passwordEncoder) {
        this.clientProperties = clientProperties;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()")
                .allowFormAuthenticationForClients();
//                .checkTokenAccess("isAuthenticated()")
//                .tokenKeyAccess("isAuthenticated()")
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // TODO
        clients.inMemory()
                .withClient("zyw")
                .secret(passwordEncoder.encode("zywSecret"))
                .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                .scopes("all")
                .accessTokenValiditySeconds(600)
                .refreshTokenValiditySeconds(7200)
                .redirectUris("http://www.dcenter.top/auth/callback")
                .scopes("all")
                .autoApprove(true)
                .autoApprove("all")
                .resourceIds("zyw");
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // Do nothing

    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
//        http.oauth2Client()
//                .clientRegistrationRepository(clientRegistrationRepository());
    }

    @Override
    public Map<String, Map<String, Set<String>>> getAuthorizeRequestMap() {
        final Map<String, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put("/oauth/token", null);
        permitAllMap.put("/oauth/authorize", null);

        Map<String, Map<String, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.permitAll, permitAllMap);

        return resultMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<String> ignoringAntMatcherUrls = clientProperties.getCsrf().getIgnoringAntMatcherUrls();
        ignoringAntMatcherUrls.add("/oauth/token");

    }


//    @Bean
//    public UserDetailsService users() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user1")
//                .password("user1")
//                .roles("USER")
//                .build();
//        return  new InMemoryUserDetailsManager(user);
//    }

//    @Bean
//    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
//        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
//                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
//        return WebClient.builder()
//                .apply(oauth2Client.oauth2Configuration())
//                .build();
//    }

//    @Bean
//    OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
//                                                          OAuth2AuthorizedClientRepository authorizedClientRepository) {
//        // TODO 配置 OAuth2 四种授权模式的开关
//        OAuth2AuthorizedClientProvider authorizedClientProvider =
//                OAuth2AuthorizedClientProviderBuilder.builder()
//                        .authorizationCode()
//                        .refreshToken()
//                        .clientCredentials()
//                        .password()
//                        .build();
//        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
//                clientRegistrationRepository, authorizedClientRepository);
//        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//        // For the `password` grant, the `username` and `password` are supplied via request parameters,
//        // so map it to `OAuth2AuthorizationContext.getAttributes()`.
//        authorizedClientManager.setContextAttributesMapper(contextAttributesMapper());
//
//        return authorizedClientManager;
//    }
//
//
//
//
//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        ClientRegistration registrations = ClientRegistration.withRegistrationId(UUID.randomUUID().toString())
//                .clientId("zyw")
//                .clientSecret("zywSecret")
//                .clientName("zyw")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationUri("/auth/authorizer")
//                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
//                .scope("all")
//                .tokenUri("/auth/token")
//                .redirectUriTemplate("http://www.dcenter.top/**")
//                .userInfoUri("/user/me")
//                .userNameAttributeName("username")
//                .build();
//        ClientRegistrationRepository clientRegistrationRepository =
//                new InMemoryClientRegistrationRepository(registrations);
//        return clientRegistrationRepository;
//    }
//
//    @Bean
//    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
//        return new HttpSessionOAuth2AuthorizedClientRepository();
//    }
//
//    @Bean
//    OAuth2AuthorizedClientManager authorizedClientManager() {
//        // TODO 配置 OAuth2 四种授权模式的开关
//        OAuth2AuthorizedClientProvider authorizedClientProvider =
//                OAuth2AuthorizedClientProviderBuilder.builder()
//                        .authorizationCode()
//                        .refreshToken()
//                        .clientCredentials()
//                        .password()
//                        .build();
//
//        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
//                clientRegistrationRepository(), authorizedClientRepository());
//        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//        // For the `password` grant, the `username` and `password` are supplied via request parameters,
//        // so map it to `OAuth2AuthorizationContext.getAttributes()`.
//        authorizedClientManager.setContextAttributesMapper(contextAttributesMapper());
//
//        return authorizedClientManager;
//    }
//
//    private Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper() {
//        return authorizeRequest -> {
//            Map<String, Object> contextAttributes = Collections.emptyMap();
//            HttpServletRequest servletRequest = authorizeRequest.getAttribute(HttpServletRequest.class.getName());
//            String username = servletRequest.getParameter(OAuth2ParameterNames.USERNAME);
//            String password = servletRequest.getParameter(OAuth2ParameterNames.PASSWORD);
//            if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
//                contextAttributes = new HashMap<>();
//
//                // `PasswordOAuth2AuthorizedClientProvider` requires both attributes
//                contextAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username);
//                contextAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password);
//            }
//            return contextAttributes;
//        };
//    }
}

package top.dcenter.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.api.config.HttpSecurityAware;
import top.dcenter.security.core.api.logout.DefaultLogoutSuccessHandler;
import top.dcenter.security.core.api.service.AbstractUserDetailsService;
import top.dcenter.security.core.auth.filter.JsonRequestFilter;
import top.dcenter.security.core.auth.provider.UsernamePasswordAuthenticationProvider;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static top.dcenter.security.core.api.config.HttpSecurityAware.*;
import static top.dcenter.security.core.consts.SecurityConstants.SERVLET_CONTEXT_AUTHORIZE_REQUESTS_MAP_KEY;

/**
 * 核心 HttpSecurity 安全相关配置
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 13:14
 * @author zyw
 */
@SuppressWarnings("jol")
@Configuration
@AutoConfigureAfter({
        SecurityConfiguration.class,
        SmsCodeLoginAuthenticationConfigurerAware.class,
        ClientConfigurerAware.class,
        CsrfConfigurerAware.class,
        RememberMeConfigurerAware.class,
        SessionConfigurerAware.class,
        ValidateCodeConfigurerAware.class})
@Slf4j
public class SecurityCoreConfigurer extends WebSecurityConfigurerAdapter {

    private final ClientProperties clientProperties;
    private final BaseAuthenticationSuccessHandler  baseAuthenticationSuccessHandler;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final ServletWebServerApplicationContext servletWebServerApplicationContext;
    private final ObjectMapper objectMapper;
    private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    private final DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;
    private final PasswordEncoder passwordEncoder;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Map<String, HttpSecurityAware> socialWebSecurityConfigurerMap;
    @Autowired
    private AbstractUserDetailsService abstractUserDetailsService;

    public SecurityCoreConfigurer(ClientProperties clientProperties,
                                  BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                  BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                  ObjectMapper objectMapper,
                                  ServletWebServerApplicationContext servletWebServerApplicationContext,
                                  UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider, DefaultLogoutSuccessHandler defaultLogoutSuccessHandler, PasswordEncoder passwordEncoder) {
        this.clientProperties = clientProperties;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.servletWebServerApplicationContext = servletWebServerApplicationContext;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.usernamePasswordAuthenticationProvider = usernamePasswordAuthenticationProvider;
        this.defaultLogoutSuccessHandler = defaultLogoutSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        String[] ignoringUrls = clientProperties.getIgnoringUrls();
        web.ignoring()
                .antMatchers(Objects.requireNonNullElseGet(ignoringUrls, () -> new String[0]));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(abstractUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 把权限类型: permitAll, denyAll, anonymous, authenticated, fullyAuthenticated, rememberMe 放入 authorizeRequestMap
        Map<String, Set<String>> authorizeRequestMap = new HashMap<>(16);
        // 把权限类型: hasRole, hasAnyRole, hasAuthority, hasAnyAuthority, hasIpAddress 放入 authorizeRequestMapPlus
        Map<String, Map<String, Set<String>>> authorizeRequestMapPlus = new HashMap<>(16);

        /* 对所有的AuthorizeRequestUris 进行分类，放入对应的 Map
         * 把从 HttpSecurityAware#getAuthorizeRequestMap() 获取的 authorizeRequestMap 根据权限分类进行合并,
         * 把权限类型: permitAll, denyAll, anonymous, authenticated, fullyAuthenticated, rememberMe 放入 authorizeRequestMap
         * 把权限类型: hasRole, hasAnyRole, hasAuthority, hasAnyAuthority, hasIpAddress 放入 authorizeRequestMapPlus
         */
        groupingAuthorizeRequestUris(http, authorizeRequestMap, authorizeRequestMapPlus);

        // 将 AuthorizeRequestUriSet 转换为对应的 array
        String[] permitAllArray = set2ArrayByType(authorizeRequestMap, permitAll);
        String[] denyAllArray = set2ArrayByType(authorizeRequestMap, denyAll);
        String[] anonymousArray = set2ArrayByType(authorizeRequestMap, anonymous);
        String[] authenticatedArray = set2ArrayByType(authorizeRequestMap, authenticated);
        String[] fullyAuthenticatedArray = set2ArrayByType(authorizeRequestMap, fullyAuthenticated);
        String[] rememberMeArray = set2ArrayByType(authorizeRequestMap, rememberMe);

        // 将 AuthorizeRequestUriMap<String, Set<String>> 转换为对应的 Map<String, String[]>
        Map<String, String[]> hasRoleMap = toMapPlusByType(authorizeRequestMapPlus, hasRole);
        Map<String, String[]> hasAnyRoleMap = toMapPlusByType(authorizeRequestMapPlus, hasAnyRole);
        Map<String, String[]> hasAuthorityMap = toMapPlusByType(authorizeRequestMapPlus, hasAuthority);
        Map<String, String[]> hasAnyAuthorityMap = toMapPlusByType(authorizeRequestMapPlus, hasAnyAuthority);
        Map<String, String[]> hasIpAddressMap = toMapPlusByType(authorizeRequestMapPlus, hasIpAddress);



        // 添加 JsonRequestFilter 增加对 Ajax 格式与 form 格式的解析,
        http.addFilterBefore(new JsonRequestFilter(objectMapper), CsrfFilter.class);


        // 判断是否开启根据不同的uri跳转到相对应的登录页, 假设开启
        String loginUnAuthenticationUrl = clientProperties.getLoginUnAuthenticationUrl();
        if (!clientProperties.getOpenAuthenticationRedirect())
        {
            // 没有开启根据不同的uri跳转到相对应的登录页, 直接跳转到登录页
            loginUnAuthenticationUrl = clientProperties.getLogoutUrl();
        }
        /* 用户密码登录的 Provider, 只是对 org.springframework.security.auth.dao.DaoAuthenticationProvider 的 copy.
         * 替换 org.springframework.security.auth.dao.DaoAuthenticationProvider 的一个原因是:
         * 当有 IOC 容器中有多个 UserDetailsService 时, org.springframework.security.auth.dao
         * .DaoAuthenticationProvider 会失效.
         * 如果要对前端传过来的密码进行解密,则请实现 UserDetailsPasswordService
         */
        http.authenticationProvider(usernamePasswordAuthenticationProvider)
                .formLogin()
                .usernameParameter(clientProperties.usernameParameter)
                .passwordParameter(clientProperties.passwordParameter)
                .loginPage(loginUnAuthenticationUrl)
                // uri 需要自己实现
                .failureUrl(clientProperties.getFailureUrl())
                .defaultSuccessUrl(clientProperties.getSuccessUrl())
                // 由 Spring Security 接管，不用任何处理
                .loginProcessingUrl(clientProperties.getLoginProcessingUrl())
                // 语句位置更重要, 放在 failureUrl()与defaultSuccessUrl()之前会失效
                .successHandler(baseAuthenticationSuccessHandler)
                .failureHandler(baseAuthenticationFailureHandler);


        // 配置 uri 验证与授权信息
        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.authorizeRequests();
        expressionInterceptUrlRegistry
            .antMatchers(permitAllArray).permitAll()
            .antMatchers(denyAllArray).denyAll()
            .antMatchers(anonymousArray).anonymous()
            .antMatchers(authenticatedArray).authenticated()
            .antMatchers(fullyAuthenticatedArray).fullyAuthenticated()
            .antMatchers(rememberMeArray).rememberMe();

        hasRoleMap.forEach((uri, roleArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasRole(roleArr[0]));
        hasAnyRoleMap.forEach((uri, roleArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasAnyRole(roleArr));
        hasAuthorityMap.forEach((uri, authorityArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasAuthority(authorityArr[0]));
        hasAnyAuthorityMap.forEach((uri, authorityArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasAnyAuthority(authorityArr));
        hasIpAddressMap.forEach((uri, ipArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasIpAddress(ipArr[0]));

        expressionInterceptUrlRegistry
            .anyRequest()
            .authenticated();

        // logout
        http.logout()
                .logoutUrl(clientProperties.getLogoutUrl())
                .logoutSuccessHandler(defaultLogoutSuccessHandler)
                .logoutSuccessUrl(clientProperties.getLogoutSuccessUrl())
                .deleteCookies(clientProperties.getRememberMe().getRememberMeCookieName(),
                           clientProperties.getSession().getSessionCookieName())
                .clearAuthentication(true)
                .invalidateHttpSession(true);

        // 允许来自同一来源(如: example.com)的请求
        http.headers().frameOptions().sameOrigin();


        if (socialWebSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware postConfigurer : socialWebSecurityConfigurerMap.values())
            {
                postConfigurer.postConfigure(http);
            }
        }
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * 把指定 authorizeRequestType 类型的 Set 转成 Array
     * @param authorizeRequestMap   存储种 authorizeRequestType 的 map
     * @param authorizeRequestType  authorizeRequestType
     * @return  当 authorizeRequestType 对应的 Set 为 null 时, 返回 new String[0];
     */
    private String[] set2ArrayByType(Map<String, Set<String>> authorizeRequestMap, String authorizeRequestType) {
        Set<String> set = authorizeRequestMap.get(authorizeRequestType);
        if (set != null)
        {
            String[] uriArray = new String[set.size()];
            set.toArray(uriArray);
            if (log.isDebugEnabled())
            {
                log.debug("{} = {}", authorizeRequestType, Arrays.toString(uriArray));
            }
            return uriArray;

        }
        return new String[0];
    }

    /**
     * 把指定 authorizeRequestType 类型获取对应的 Map
     * @param authorizeRequestMap   存储种 authorizeRequestType 的 map
     * @param authorizeRequestType  authorizeRequestType
     * @return  当 authorizeRequestType 对应的 Map 为 null 时, 返回 new hashMap<>(0);
     */
    private Map<String, String[]> toMapPlusByType(Map<String, Map<String, Set<String>>> authorizeRequestMap, String authorizeRequestType) {
        Map<String, Set<String>> map = authorizeRequestMap.get(authorizeRequestType);
        if (map != null)
        {
            Map<String, String[]> resultMap =
                    map.entrySet()
                       .stream()
                       .collect(Collectors.toMap(entry -> entry.getKey(),
                                                 entry ->
                                                 {
                                                     Set<String> value = entry.getValue();
                                                     int length = value.size();
                                                     String[] authorityArr = new String[length];
                                                     return value.toArray(authorityArr);
                                                 }));
            return resultMap;

        }
        return new HashMap<>(0);
    }

    /**
     * 把从 {@link HttpSecurityAware#getAuthorizeRequestMap()} 获取的 Map 根据权限分类进行合并,
     * 把权限作为 key 与之相对应的 uriSet 作为 value, 分类放入 map, 此 map 存储在 applicationContent 时所用的 key
     * 传入参数都不能为 null.
     * @param http  HttpSecurity
     * @param targetAuthorizeRequestMap  用于存储 permitAll, denyAll, anonymous, authenticated, fullyAuthenticated,
     *                                   rememberMe 的权限类型.
     * @param targetAuthorizeRequestMapPlus  用于存储 hasRole, hasAnyRole, hasAuthority, hasAnyAuthority, hasIpAddress
     *                                       的权限类型.
     * @throws Exception  Exception
     */
    private void groupingAuthorizeRequestUris(@NonNull HttpSecurity http,
                                              @NonNull Map<String, Set<String>> targetAuthorizeRequestMap,
                                              @NonNull Map<String, Map<String, Set<String>>> targetAuthorizeRequestMapPlus) throws Exception {
        if (this.socialWebSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware configurer : this.socialWebSecurityConfigurerMap.values())
            {
                configurer.preConfigure(http);
                Map<String, Map<String, Set<String>>> authorizeRequestMap = configurer.getAuthorizeRequestMap();
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, permitAll);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, denyAll);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, anonymous);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, authenticated);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, fullyAuthenticated);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, rememberMe);

                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, hasRole);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, hasAnyRole);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, hasAuthority);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, hasAnyAuthority);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, hasIpAddress);
            }

            // 把 targetAuthorizeRequestMap 添加到 ServletContext, 主要用于 SessionEnhanceCheckFilter
            Objects.requireNonNull(servletWebServerApplicationContext.getServletContext())
                    .setAttribute(SERVLET_CONTEXT_AUTHORIZE_REQUESTS_MAP_KEY, targetAuthorizeRequestMap);
        }
    }


    /**
     * 根据权限类型从 authorizeRequestMap 提取的 uriSet 添加到 map 中, 权限仅限制为:
     * permitAll, denyAll, anonymous, authenticated, fullyAuthenticated, rememberMe 的类型.
     * @param targetAuthorizeRequestMap 不可以为null
     * @param srcAuthorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     */
    private void groupByMap(@NonNull Map<String, Set<String>> targetAuthorizeRequestMap,
                            @Nullable Map<String, Map<String, Set<String>>> srcAuthorizeRequestMap,
                            @NonNull String authorizeRequestType) {

        if (srcAuthorizeRequestMap != null)
        {
            Map<String, Set<String>> stringSetMap = srcAuthorizeRequestMap.get(authorizeRequestType);
            if (stringSetMap == null)
            {
                return;
            }

            Set<String> set = stringSetMap.keySet();
            if (set == null)
            {
                set = new HashSet<>();
            }
            final Set<String> uriSet = set;

            targetAuthorizeRequestMap.compute(authorizeRequestType, (k, v) -> {

                if (v == null)
                {
                    v = new HashSet<>(uriSet.size());
                }
                v.addAll(uriSet);
                return v;
            });
        }
    }

    /**
     * 根据权限类型从 authorizeRequestMap 提取的 uriSet 添加到 map 中, 权限仅限制为:
     * hasRole, hasAnyRole, hasAuthority, hasAnyAuthority, hasIpAddress 的类型.
     * @param targetAuthorizeRequestMap 不可以为null
     * @param srcAuthorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     */
    private void groupByMapPlus(@NonNull Map<String, Map<String, Set<String>>> targetAuthorizeRequestMap,
                            @Nullable Map<String, Map<String, Set<String>>> srcAuthorizeRequestMap,
                            @NonNull String authorizeRequestType) {

        if (srcAuthorizeRequestMap != null)
        {
            Map<String, Set<String>> map = srcAuthorizeRequestMap.get(authorizeRequestType);
            if (map == null)
            {
                map = new HashMap<>(0);
            }
            final Map<String, Set<String>> uriAuthorizeMap = map;

            targetAuthorizeRequestMap.compute(authorizeRequestType, (k, v) -> {

                if (v == null)
                {
                    v = new HashMap<>(uriAuthorizeMap.size());
                }
                v.putAll(uriAuthorizeMap);
                return v;
            });
        }
    }

}


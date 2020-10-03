package top.dcenter.ums.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.context.WebApplicationContext;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.api.logout.DefaultLogoutSuccessHandler;
import top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.filter.AjaxOrFormRequestFilter;
import top.dcenter.ums.security.core.auth.provider.UsernamePasswordAuthenticationProvider;
import top.dcenter.ums.security.core.consts.SecurityConstants;
import top.dcenter.ums.security.core.properties.ClientProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static top.dcenter.ums.security.core.api.config.HttpSecurityAware.*;

/**
 * 核心 HttpSecurity 安全相关配置
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 13:14
 * @author zyw
 */
@SuppressWarnings("jol")
@Configuration
@EnableWebSecurity
@AutoConfigureAfter({
        SecurityAutoConfiguration.class,
        SmsCodeLoginAuthenticationAutoConfigurerAware.class,
        ClientAutoConfigurerAware.class,
        CsrfAutoConfigurerAware.class,
        RememberMeAutoConfigurerAware.class,
        SessionAutoConfigurerAware.class,
        ValidateCodeAutoConfigurerAware.class})
@Slf4j
public class SecurityCoreAutoConfigurer extends WebSecurityConfigurerAdapter {

    private final ClientProperties clientProperties;
    private final BaseAuthenticationSuccessHandler  baseAuthenticationSuccessHandler;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final ObjectMapper objectMapper;
    private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    private final DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;
    private final PasswordEncoder passwordEncoder;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Map<String, HttpSecurityAware> socialWebSecurityConfigurerMap;
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired(required = false)
    private UmsUserDetailsService umsUserDetailsService;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private UriAuthorizeService uriAuthorizeService;

    public SecurityCoreAutoConfigurer(ClientProperties clientProperties,
                                      BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                      BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                      ObjectMapper objectMapper,
                                      UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider, DefaultLogoutSuccessHandler defaultLogoutSuccessHandler, PasswordEncoder passwordEncoder) {
        this.clientProperties = clientProperties;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.usernamePasswordAuthenticationProvider = usernamePasswordAuthenticationProvider;
        this.defaultLogoutSuccessHandler = defaultLogoutSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void configure(WebSecurity web) {
        String[] ignoringUrls = clientProperties.getIgnoringUrls();
        web.ignoring()
                .antMatchers(Objects.requireNonNullElseGet(ignoringUrls, () -> new String[0]));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (umsUserDetailsService == null)
        {
            throw new RuntimeException("必须实现 UmsUserDetailsService 或 top.dcenter.security.social.api.service.UmsSocialUserDetailsService 抽象类");
        }
        auth.userDetailsService(umsUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 把权限类型: PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED, REMEMBER_ME 放入 authorizeRequestMap
        Map<String, Set<String>> authorizeRequestMap = new HashMap<>(16);
        // 把权限类型: HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS 放入 authorizeRequestMapPlus
        Map<String, Map<String, Set<String>>> authorizeRequestMapPlus = new HashMap<>(16);

        /* 对所有的AuthorizeRequestUris 进行分类，放入对应的 Map
         * 把从 HttpSecurityAware#getAuthorizeRequestMap() 获取的 authorizeRequestMap 根据权限分类进行合并,
         * 把权限类型: PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED, REMEMBER_ME 放入 authorizeRequestMap
         * 把权限类型: HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS 放入 authorizeRequestMapPlus
         */
        groupingAuthorizeRequestUris(http, authorizeRequestMap, authorizeRequestMapPlus);

        // 将 AuthorizeRequestUriSet 转换为对应的 array
        String[] permitAllArray = set2ArrayByType(authorizeRequestMap, PERMIT_ALL);
        String[] denyAllArray = set2ArrayByType(authorizeRequestMap, DENY_ALL);
        String[] anonymousArray = set2ArrayByType(authorizeRequestMap, ANONYMOUS);
        String[] authenticatedArray = set2ArrayByType(authorizeRequestMap, AUTHENTICATED);
        String[] fullyAuthenticatedArray = set2ArrayByType(authorizeRequestMap, FULLY_AUTHENTICATED);
        String[] rememberMeArray = set2ArrayByType(authorizeRequestMap, REMEMBER_ME);
        String[] accessArray = set2ArrayByType(authorizeRequestMap, ACCESS);

        // 将 AuthorizeRequestUriMap<String, Set<String>> 转换为对应的 Map<uri, role[]>
        Map<String, String[]> hasRoleMap = toMapPlusByType(authorizeRequestMapPlus, HAS_ROLE);
        Map<String, String[]> hasAnyRoleMap = toMapPlusByType(authorizeRequestMapPlus, HAS_ANY_ROLE);
        Map<String, String[]> hasAuthorityMap = toMapPlusByType(authorizeRequestMapPlus, HAS_AUTHORITY);
        Map<String, String[]> hasAnyAuthorityMap = toMapPlusByType(authorizeRequestMapPlus, HAS_ANY_AUTHORITY);
        Map<String, String[]> hasIpAddressMap = toMapPlusByType(authorizeRequestMapPlus, HAS_IP_ADDRESS);

        // 添加 AjaxOrFormRequestFilter 增加对 Ajax 格式与 form 格式的解析,
        http.addFilterBefore(new AjaxOrFormRequestFilter(objectMapper), CsrfFilter.class);

        // 判断是否开启根据不同的uri跳转到相对应的登录页, 假设开启
        String loginUnAuthenticationRoutingUrl = clientProperties.getLoginUnAuthenticationRoutingUrl();
        if (!clientProperties.getOpenAuthenticationRedirect())
        {
            // 没有开启根据不同的uri跳转到相对应的登录页, 直接跳转到登录页
            loginUnAuthenticationRoutingUrl = clientProperties.getLogoutUrl();
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
                .loginPage(loginUnAuthenticationRoutingUrl)
                // uri 需要自己实现
                .failureUrl(clientProperties.getFailureUrl())
                .defaultSuccessUrl(clientProperties.getSuccessUrl())
                // 由 Spring Security 接管，不用任何处理
                .loginProcessingUrl(clientProperties.getLoginProcessingUrl())
                // 语句位置更重要, 放在 failureUrl()与defaultSuccessUrl()之前会失效
                .successHandler(baseAuthenticationSuccessHandler)
                .failureHandler(baseAuthenticationFailureHandler);

        // 匿名用户配置
        anonymousConfigurer(http);

        // 配置 uri 验证与授权信息
        urlAuthorizationConfigurer(http, permitAllArray, denyAllArray, anonymousArray, authenticatedArray,
                                   fullyAuthenticatedArray, rememberMeArray, accessArray, hasRoleMap, hasAnyRoleMap,
                                   hasAuthorityMap, hasAnyAuthorityMap, hasIpAddressMap);


        // logout
        logoutConfigurer(http);

        // 允许来自同一来源(如: example.com)的请求
        if (clientProperties.getSameOrigin())
        {
            http.headers().frameOptions().sameOrigin();
        }

        if (socialWebSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware postConfigurer : socialWebSecurityConfigurerMap.values())
            {
                postConfigurer.postConfigure(http);
            }
        }
    }

    private void anonymousConfigurer(HttpSecurity http) throws Exception {
        ClientProperties.AnonymousProperties anonymous = clientProperties.getAnonymous();
        String[] authorities = new String[anonymous.getAuthorities().size()];
        anonymous.getAuthorities().toArray(authorities);
        if (anonymous.getAnonymousIsOpen())
        {
            http.anonymous()
                .principal(anonymous.getPrincipal())
                .authorities(authorities);
        }
        else
        {
            http.anonymous().disable();
        }
    }

    private void logoutConfigurer(HttpSecurity http) throws Exception {
        http.logout()
                .logoutUrl(clientProperties.getLogoutUrl())
                .logoutSuccessHandler(defaultLogoutSuccessHandler)
                .logoutSuccessUrl(clientProperties.getLogoutSuccessUrl())
                .deleteCookies(clientProperties.getRememberMe().getRememberMeCookieName(),
                           clientProperties.getSession().getSessionCookieName())
                .clearAuthentication(true)
                .invalidateHttpSession(true);
    }

    private void urlAuthorizationConfigurer(HttpSecurity http) throws Exception {
        Optional<Map<String, Set<String>>> uriAuthoritiesOfAllRole = uriAuthorizeService.getUriAuthoritiesOfAllRole();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();

    }

    private void urlAuthorizationConfigurer(HttpSecurity http, String[] permitAllArray,
                                            String[] denyAllArray, String[] anonymousArray,
                                            String[] authenticatedArray, String[] fullyAuthenticatedArray,
                                            String[] rememberMeArray, String[] accessArray, Map<String, String[]> hasRoleMap,
                                            Map<String, String[]> hasAnyRoleMap, Map<String, String[]> hasAuthorityMap,
                                            Map<String, String[]> hasAnyAuthorityMap, Map<String, String[]> hasIpAddressMap) throws Exception {

        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.authorizeRequests();
        expressionInterceptUrlRegistry
            .antMatchers(permitAllArray).permitAll()
            .antMatchers(denyAllArray).denyAll()
            .antMatchers(anonymousArray).anonymous()
            .antMatchers(authenticatedArray).authenticated()
            .antMatchers(fullyAuthenticatedArray).fullyAuthenticated()
            .antMatchers(rememberMeArray).rememberMe();

        hasRoleMap.forEach((uri, roleArr) -> {
                    for (String role : roleArr)
                    {
                        expressionInterceptUrlRegistry.antMatchers(uri).hasRole(role);
                    }
                });

        hasAnyRoleMap.forEach((uri, roleArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasAnyRole(roleArr));

        hasAuthorityMap.forEach(
                (uri, authorityArr) -> {
                    for (String s : authorityArr)
                    {
                        expressionInterceptUrlRegistry.antMatchers(uri).hasAuthority(s);
                    }
                });

        hasAnyAuthorityMap.forEach((uri, authorityArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasAnyAuthority(authorityArr));

        hasIpAddressMap.forEach(
                (uri, ipArr) -> {
                    for (String s : ipArr)
                    {
                        expressionInterceptUrlRegistry.antMatchers(uri).hasIpAddress(s);
                    }
                });


        if (accessArray.length > 0)
        {
            StringBuilder sb = new StringBuilder();
            for (String access : accessArray)
            {
                if (StringUtils.isNotBlank(access))
                {
                    sb.append(access).append(" and ");
                }
            }
            int interceptLen = 5;
            if (sb.length() > interceptLen)
            {
                sb.setLength(sb.length() - interceptLen);
                expressionInterceptUrlRegistry.anyRequest().access(sb.toString());
                return;
            }
        }

        expressionInterceptUrlRegistry.anyRequest().authenticated();

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
     * 从 authorizeRequestMap 中获取指定 authorizeRequestType 类型的 Map
     * @param authorizeRequestMap   存储 authorizeRequestType 的 map
     * @param authorizeRequestType  authorizeRequestType
     * @return  返回map<uri, role[]>, 当 authorizeRequestType 对应的 Map 为 null 时, 返回 new hashMap<>(0);
     */
    private Map<String, String[]> toMapPlusByType(Map<String, Map<String, Set<String>>> authorizeRequestMap, String authorizeRequestType) {
        Map<String, Set<String>> map = authorizeRequestMap.get(authorizeRequestType);
        if (map != null)
        {
            return map.entrySet()
               .stream()
               .collect(Collectors.toMap(Map.Entry::getKey,
                                         entry ->
                                         {
                                             Set<String> value = entry.getValue();
                                             int length = value.size();
                                             String[] authorityArr = new String[length];
                                             return value.toArray(authorityArr);
                                         }));

        }
        return new HashMap<>(0);
    }

    /**
     * 把从 {@link HttpSecurityAware#getAuthorizeRequestMap()} 获取的 Map 根据权限分类进行合并,
     * 把权限作为 key 与之相对应的 uriSet 作为 value, 分类放入 map, 此 map 存储在 applicationContent 时所用的 key
     * 传入参数都不能为 null.
     * @param http  HttpSecurity
     * @param targetAuthorizeRequestMap  用于存储 PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED,
     *                                   REMEMBER_ME 的权限类型.
     * @param targetAuthorizeRequestMapPlus  用于存储 HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS
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
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, PERMIT_ALL);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, DENY_ALL);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, ANONYMOUS);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, AUTHENTICATED);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, FULLY_AUTHENTICATED);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, REMEMBER_ME);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, ACCESS);

                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, HAS_ROLE);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, HAS_ANY_ROLE);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, HAS_AUTHORITY);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, HAS_ANY_AUTHORITY);
                groupByMapPlus(targetAuthorizeRequestMapPlus, authorizeRequestMap, HAS_IP_ADDRESS);
            }

            ApplicationContext applicationContext = getApplicationContext();
            WebApplicationContext servletContext = (WebApplicationContext) applicationContext;

            // 把 targetAuthorizeRequestMap 添加到 ServletContext, 主要用于 AuthenticationUtil.isPermitAll(..)
            Objects.requireNonNull(servletContext.getServletContext())
                    .setAttribute(SecurityConstants.SERVLET_CONTEXT_AUTHORIZE_REQUESTS_MAP_KEY, targetAuthorizeRequestMap);
        }
    }


    /**
     * 根据权限类型从 authorizeRequestMap 提取的 uriSet 添加到 map 中, 权限仅限制为:
     * PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED, REMEMBER_ME 的类型.
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

            final Set<String> uriSet = stringSetMap.keySet();

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
     * HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS 的类型.
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


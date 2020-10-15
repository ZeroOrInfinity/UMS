package top.dcenter.ums.security.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static top.dcenter.ums.security.common.api.config.HttpSecurityAware.*;

/**
 * 核心 HttpSecurity 安全相关配置
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 13:14
 * @author zyw
 */
@Configuration
@Order(99)
@EnableWebSecurity
public class SecurityCoreAutoConfigurer extends WebSecurityConfigurerAdapter {

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Map<String, HttpSecurityAware> webSecurityConfigurerMap;

    @Override
    public void configure(WebSecurity web) {
        if (webSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware postConfigurer : webSecurityConfigurerMap.values())
            {
                postConfigurer.configure(web);
            }
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (webSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware postConfigurer : webSecurityConfigurerMap.values())
            {
                postConfigurer.configure(auth);
            }
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 把权限类型: PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED, REMEMBER_ME 放入 authorizeRequestMap
        Map<String, Set<UriHttpMethodTuple>> authorizeRequestMap = new HashMap<>(16);
        // 把权限类型: HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS 放入 authorizeRequestMapPlus
        Map<String, Map<UriHttpMethodTuple, Set<String>>> authorizeRequestMapPlus = new HashMap<>(16);

        /* 对所有的AuthorizeRequestUris 进行分类，放入对应的 Map
         * 把从 HttpSecurityAware#getAuthorizeRequestMap() 获取的 authorizeRequestMap 根据权限分类进行合并,
         * 把权限类型: PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED, REMEMBER_ME 放入 authorizeRequestMap
         * 把权限类型: HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS 放入 authorizeRequestMapPlus
         */
        groupingAuthorizeRequestUris(authorizeRequestMap, authorizeRequestMapPlus);

        // 将 AuthorizeRequestUriSet 转换为对应的 array
        UriHttpMethodTuple[] permitAllArray = set2ArrayByType(authorizeRequestMap, PERMIT_ALL);
        UriHttpMethodTuple[] denyAllArray = set2ArrayByType(authorizeRequestMap, DENY_ALL);
        UriHttpMethodTuple[] anonymousArray = set2ArrayByType(authorizeRequestMap, ANONYMOUS);
        UriHttpMethodTuple[] authenticatedArray = set2ArrayByType(authorizeRequestMap, AUTHENTICATED);
        UriHttpMethodTuple[] fullyAuthenticatedArray = set2ArrayByType(authorizeRequestMap, FULLY_AUTHENTICATED);
        UriHttpMethodTuple[] rememberMeArray = set2ArrayByType(authorizeRequestMap, REMEMBER_ME);
        UriHttpMethodTuple[] accessArray = set2ArrayByType(authorizeRequestMap, ACCESS);

        // 将 AuthorizeRequestUriMap<String, Set<String>> 转换为对应的 Map<uri, role[]>
        Map<UriHttpMethodTuple, String[]> hasRoleMap = toMapPlusByType(authorizeRequestMapPlus, HAS_ROLE);
        Map<UriHttpMethodTuple, String[]> hasAnyRoleMap = toMapPlusByType(authorizeRequestMapPlus, HAS_ANY_ROLE);
        Map<UriHttpMethodTuple, String[]> hasAuthorityMap = toMapPlusByType(authorizeRequestMapPlus, HAS_AUTHORITY);
        Map<UriHttpMethodTuple, String[]> hasAnyAuthorityMap = toMapPlusByType(authorizeRequestMapPlus, HAS_ANY_AUTHORITY);
        Map<UriHttpMethodTuple, String[]> hasIpAddressMap = toMapPlusByType(authorizeRequestMapPlus, HAS_IP_ADDRESS);

        // 处理 preConfigure
        if (webSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware postConfigurer : webSecurityConfigurerMap.values())
            {
                postConfigurer.preConfigure(http);
            }
        }

        // 配置 权限 验证 授权 信息
        urlAuthorizationConfigurer(http, permitAllArray, denyAllArray, anonymousArray, authenticatedArray,
                                   fullyAuthenticatedArray, rememberMeArray, accessArray, hasRoleMap, hasAnyRoleMap,
                                   hasAuthorityMap, hasAnyAuthorityMap, hasIpAddressMap);

        // 处理 postConfigure
        if (webSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware postConfigurer : webSecurityConfigurerMap.values())
            {
                postConfigurer.postConfigure(http);
            }
        }
    }

    private void urlAuthorizationConfigurer(HttpSecurity http, UriHttpMethodTuple[] permitAllArray,
                                            UriHttpMethodTuple[] denyAllArray, UriHttpMethodTuple[] anonymousArray,
                                            UriHttpMethodTuple[] authenticatedArray, UriHttpMethodTuple[] fullyAuthenticatedArray,
                                            UriHttpMethodTuple[] rememberMeArray, UriHttpMethodTuple[] accessArray, Map<UriHttpMethodTuple, String[]> hasRoleMap,
                                            Map<UriHttpMethodTuple, String[]> hasAnyRoleMap, Map<UriHttpMethodTuple, String[]> hasAuthorityMap,
                                            Map<UriHttpMethodTuple, String[]> hasAnyAuthorityMap, Map<UriHttpMethodTuple, String[]> hasIpAddressMap) throws Exception {

        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();

        setAuthorizeRequest(registry, permitAllArray, PERMIT_ALL);
        setAuthorizeRequest(registry, denyAllArray, DENY_ALL);
        setAuthorizeRequest(registry, anonymousArray, ANONYMOUS);
        setAuthorizeRequest(registry, authenticatedArray, AUTHENTICATED);
        setAuthorizeRequest(registry, fullyAuthenticatedArray, FULLY_AUTHENTICATED);
        setAuthorizeRequest(registry, rememberMeArray, REMEMBER_ME);


        setAuthorizeRequestPlus(registry, hasRoleMap, HAS_ROLE);
        setAuthorizeRequestPlus(registry, hasAnyRoleMap, HAS_ANY_ROLE);
        setAuthorizeRequestPlus(registry, hasAuthorityMap, HAS_AUTHORITY);
        setAuthorizeRequestPlus(registry, hasAnyAuthorityMap, HAS_ANY_AUTHORITY);
        setAuthorizeRequestPlus(registry, hasIpAddressMap, HAS_IP_ADDRESS);

        if (accessArray.length > 0)
        {
            StringBuilder sb = new StringBuilder();
            for (UriHttpMethodTuple tuple : accessArray)
            {
                if (tuple != null && StringUtils.hasText(tuple.getUri()))
                {
                    sb.append(tuple).append(" and ");
                }
            }
            int interceptLen = 5;
            if (sb.length() > interceptLen)
            {
                sb.setLength(sb.length() - interceptLen);
                registry.anyRequest().access(sb.toString());
                return;
            }
        }

        registry.anyRequest().authenticated();

    }

    /**
     * 根据 authorizeRequestType 设置权限
     * @param registry              ExpressionInterceptUrlRegistry
     * @param uriHttpMethodTupleMap Map&#60;UriHttpMethodTuple, String[]&#62;
     * @param authorizeRequestType  authorizeRequestType
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    private void setAuthorizeRequestPlus(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry, Map<UriHttpMethodTuple, String[]> uriHttpMethodTupleMap, String authorizeRequestType) {

        switch (authorizeRequestType)
        {
            case HAS_ROLE:
                uriHttpMethodTupleMap.forEach((tuple, roleArr) -> {
                    for (String role : roleArr)
                    {
                        HttpMethod method = tuple.getMethod();
                        if (method == null)
                        {
                            registry.antMatchers(tuple.getUri()).hasRole(role);
                        }
                        else
                        {
                            registry.antMatchers(method, tuple.getUri()).hasRole(role);
                        }
                    }
                });
                break;
            case HAS_ANY_ROLE:
                uriHttpMethodTupleMap.forEach((tuple, roleArr) -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).hasAnyRole(roleArr);
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).hasAnyRole(roleArr);
                    }
                });
                break;
            case HAS_AUTHORITY:
                uriHttpMethodTupleMap.forEach((tuple, roleArr) -> {
                    for (String role : roleArr)
                    {
                        HttpMethod method = tuple.getMethod();
                        if (method == null)
                        {
                            registry.antMatchers(tuple.getUri()).hasAuthority(role);
                        }
                        else
                        {
                            registry.antMatchers(method, tuple.getUri()).hasAuthority(role);
                        }
                    }
                });
                break;
            case HAS_ANY_AUTHORITY:
                uriHttpMethodTupleMap.forEach((tuple, roleArr) -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).hasAnyAuthority(roleArr);
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).hasAnyAuthority(roleArr);
                    }
                });
                break;
            case HAS_IP_ADDRESS:
                uriHttpMethodTupleMap.forEach((tuple, ipAddress) -> {
                    for (String ip : ipAddress)
                    {
                        HttpMethod method = tuple.getMethod();
                        if (method == null)
                        {
                            registry.antMatchers(tuple.getUri()).hasIpAddress(ip);
                        }
                        else
                        {
                            registry.antMatchers(method, tuple.getUri()).hasIpAddress(ip);
                        }
                    }
                });
                break;
            default:
                String msg = String.format("权限类型 %s 错误", authorizeRequestType);
                throw new RuntimeException(msg);
        }
    }

    /**
     * 根据 authorizeRequestType 设置权限
     * @param registry              ExpressionInterceptUrlRegistry
     * @param tuples                UriHttpMethodTuple[]
     * @param authorizeRequestType  authorizeRequestType
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    private void setAuthorizeRequest(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry, UriHttpMethodTuple[] tuples, String authorizeRequestType) {

        switch (authorizeRequestType)
        {
            case PERMIT_ALL:
                Arrays.stream(tuples).forEach(tuple -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).permitAll();
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).permitAll();
                    }
                });
                break;
            case DENY_ALL:
                Arrays.stream(tuples).forEach(tuple -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).denyAll();
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).denyAll();
                    }
                });
                break;
            case ANONYMOUS:
                Arrays.stream(tuples).forEach(tuple -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).anonymous();
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).anonymous();
                    }
                });
                break;
            case AUTHENTICATED:
                Arrays.stream(tuples).forEach(tuple -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).authenticated();
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).authenticated();
                    }
                });
                break;
            case FULLY_AUTHENTICATED:
                Arrays.stream(tuples).forEach(tuple -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).fullyAuthenticated();
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).fullyAuthenticated();
                    }
                });
                break;
            case REMEMBER_ME:
                Arrays.stream(tuples).forEach(tuple -> {
                    HttpMethod method = tuple.getMethod();
                    if (method == null)
                    {
                        registry.antMatchers(tuple.getUri()).rememberMe();
                    }
                    else
                    {
                        registry.antMatchers(method, tuple.getUri()).rememberMe();
                    }
                });
                break;
            default:
                String msg = String.format("权限类型 %s 错误", authorizeRequestType);
                throw new RuntimeException(msg);
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
     * @return  当 authorizeRequestType 对应的 Set 为 null 时, 返回 new UriHttpMethodTuple[0];
     */
    private UriHttpMethodTuple[] set2ArrayByType(Map<String, Set<UriHttpMethodTuple>> authorizeRequestMap, String authorizeRequestType) {
        Set<UriHttpMethodTuple> set = authorizeRequestMap.get(authorizeRequestType);
        if (set != null)
        {
            UriHttpMethodTuple[] uriArray = new UriHttpMethodTuple[set.size()];
            set.toArray(uriArray);

            return uriArray;

        }
        return new UriHttpMethodTuple[0];
    }

    /**
     * 从 authorizeRequestMap 中获取指定 authorizeRequestType 类型的 Map
     * @param authorizeRequestMap   存储 authorizeRequestType 的 map
     * @param authorizeRequestType  authorizeRequestType
     * @return  返回map(UriHttpMethodTuple, role[]), 当 authorizeRequestType 对应的 Map 为 null 时, 返回 new hashMap<>(0);
     */
    private Map<UriHttpMethodTuple, String[]> toMapPlusByType(Map<String, Map<UriHttpMethodTuple, Set<String>>> authorizeRequestMap, String authorizeRequestType) {
        Map<UriHttpMethodTuple, Set<String>> map = authorizeRequestMap.get(authorizeRequestType);
        if (map != null)
        {
            return map.entrySet()
               .stream()
               .collect(Collectors.toMap(Map.Entry::getKey,
                                         entry ->
                                         {
                                             Set<String> value = entry.getValue();
                                             if (value == null)
                                             {
                                                 value = new HashSet<>();
                                             }
                                             int length = value.size();
                                             String[] authorityArr = new String[length];
                                             return value.toArray(authorityArr);
                                         }));

        }
        return new HashMap<>(0);
    }

    /**
     * 对 HttpSecurity进行前置配置, 再把从 {@link HttpSecurityAware#getAuthorizeRequestMap()} 获取的 Map 根据权限分类进行合并,
     * 把权限作为 key 与之相对应的 uriSet 作为 value, 分类放入 map, 此 map 存储在 applicationContent 时所用的 key 传入参数都不能为 null.
     * @param targetAuthorizeRequestMap  用于存储 PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED,
     *                                   REMEMBER_ME 的权限类型.
     * @param targetAuthorizeRequestMapPlus  用于存储 HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS
     *                                       的权限类型.
     */
    private void groupingAuthorizeRequestUris(@NonNull Map<String, Set<UriHttpMethodTuple>> targetAuthorizeRequestMap,
                                              @NonNull Map<String, Map<UriHttpMethodTuple, Set<String>>> targetAuthorizeRequestMapPlus) {
        if (this.webSecurityConfigurerMap != null)
        {
            for (HttpSecurityAware configurer : this.webSecurityConfigurerMap.values())
            {
                Map<String, Map<UriHttpMethodTuple, Set<String>>> authorizeRequestMap = configurer.getAuthorizeRequestMap();
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
     * PERMIT_ALL, DENY_ALL, ANONYMOUS, AUTHENTICATED, FULLY_AUTHENTICATED, REMEMBER_ME, ACCESS 的类型.
     * @param targetAuthorizeRequestMap 不可以为null
     * @param srcAuthorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     */
    private void groupByMap(@NonNull Map<String, Set<UriHttpMethodTuple>> targetAuthorizeRequestMap,
                            @Nullable Map<String, Map<UriHttpMethodTuple, Set<String>>> srcAuthorizeRequestMap,
                            @NonNull String authorizeRequestType) {

        if (srcAuthorizeRequestMap != null)
        {
            Map<UriHttpMethodTuple, Set<String>> stringSetMap = srcAuthorizeRequestMap.get(authorizeRequestType);
            if (stringSetMap == null)
            {
                return;
            }

            final Set<UriHttpMethodTuple> uriSet = stringSetMap.keySet();

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
     * 根据权限类型从 authorizeRequestMap 提取的 uriSet 添加到 targetAuthorizeRequestMap 中, 权限仅限制为:
     * HAS_ROLE, HAS_ANY_ROLE, HAS_AUTHORITY, HAS_ANY_AUTHORITY, HAS_IP_ADDRESS 的类型.
     * @param targetAuthorizeRequestMap 不可以为null
     * @param srcAuthorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     */
    private void groupByMapPlus(@NonNull Map<String, Map<UriHttpMethodTuple, Set<String>>> targetAuthorizeRequestMap,
                            @Nullable Map<String, Map<UriHttpMethodTuple, Set<String>>> srcAuthorizeRequestMap,
                            @NonNull String authorizeRequestType) {

        if (srcAuthorizeRequestMap != null)
        {
            Map<UriHttpMethodTuple, Set<String>> map = srcAuthorizeRequestMap.get(authorizeRequestType);
            if (map == null)
            {
                map = new HashMap<>(0);
            }
            final Map<UriHttpMethodTuple, Set<String>> uriAuthorizeMap = map;

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


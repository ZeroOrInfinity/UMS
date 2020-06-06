package top.dcenter.security.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.api.service.CacheUserDetailsService;
import top.dcenter.security.core.auth.logout.DefaultLogoutSuccessHandler;
import top.dcenter.security.core.auth.provider.UsernamePasswordAuthenticationProvider;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static top.dcenter.security.core.api.config.WebSecurityConfigurerAware.anonymous;
import static top.dcenter.security.core.api.config.WebSecurityConfigurerAware.authenticated;
import static top.dcenter.security.core.api.config.WebSecurityConfigurerAware.denyAll;
import static top.dcenter.security.core.api.config.WebSecurityConfigurerAware.fullyAuthenticated;
import static top.dcenter.security.core.api.config.WebSecurityConfigurerAware.permitAll;
import static top.dcenter.security.core.api.config.WebSecurityConfigurerAware.rememberMe;
import static top.dcenter.security.core.consts.SecurityConstants.SERVLET_CONTEXT_AUTHORIZE_REQUESTS_MAP_KEY;

/**
 * 核心 HttpSecurity 安全相关配置
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 13:14
 * @medifiedBy zyw
 */
@SuppressWarnings("jol")
@Configuration
@AutoConfigureAfter({
        PropertiesConfiguration.class,
        SecurityConfiguration.class,
        ValidateCodeBeanConfiguration.class,
        SmsCodeLoginAuthenticationConfig.class,
        SmsCodeLoginAuthenticationConfigurerAware.class,
        ValidateCodeConfigurerAware.class})
@Slf4j
public class SecurityCoreConfigurer extends WebSecurityConfigurerAdapter {

    private final ClientProperties clientProperties;
    private final BaseAuthenticationSuccessHandler  baseAuthenticationSuccessHandler;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    private final ServletWebServerApplicationContext servletWebServerApplicationContext;
    private final ObjectMapper objectMapper;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private CacheUserDetailsService cacheUserDetailsService;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Map<String, WebSecurityConfigurerAware> socialWebSecurityConfigurerMap;

    public SecurityCoreConfigurer(ClientProperties clientProperties,
                                  BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                  BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                  UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider,
                                  ObjectMapper objectMapper,
                                  ServletWebServerApplicationContext servletWebServerApplicationContext) {
        this.clientProperties = clientProperties;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.usernamePasswordAuthenticationProvider = usernamePasswordAuthenticationProvider;
        this.servletWebServerApplicationContext = servletWebServerApplicationContext;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Map<String, Set<String>> authorizeRequestMap = new HashMap<>(16);

        // 对所有的AuthorizeRequestUris 进行分类，放入对应的 Map
        // 把从 WebSecurityConfigurerAware#getAuthorizeRequestMap() 获取的 authorizeRequestMap 根据权限分类进行合并,
        // 把权限作为 key 与之相对应的 uriSet 作为 value, 分类放入 map, 此 authorizeRequestMap 存储在 ServletContext 时所用的 key
        fillingAuthorizeRequestUris(http, authorizeRequestMap);

        // 将 AuthorizeRequestUriSet 转换为对应的 array
        String[] permitAllArray = set2Array(authorizeRequestMap, permitAll);
        String[] denyAllArray = set2Array(authorizeRequestMap, denyAll);
        String[] anonymousArray = set2Array(authorizeRequestMap, anonymous);
        String[] authenticatedArray = set2Array(authorizeRequestMap, authenticated);
        String[] fullyAuthenticatedArray = set2Array(authorizeRequestMap, fullyAuthenticated);
        String[] rememberMeArray = set2Array(authorizeRequestMap, rememberMe);


        /* 用户密码登录的 Provider, 只是对 org.springframework.security.auth.dao.DaoAuthenticationProvider 的 copy.
         * 替换 org.springframework.security.auth.dao.DaoAuthenticationProvider 的一个原因是:
         * 当有 IOC 容器中有多个 UserDetailsService 时, org.springframework.security.auth.dao
         * .DaoAuthenticationProvider 会失效.
         * 如果要对前端传过来的密码进行解密,则请实现 UserDetailsPasswordService
         */
        http.authenticationProvider(usernamePasswordAuthenticationProvider)
            .formLogin()
                // uri 需要自己实现
                .loginPage(clientProperties.getLoginUnAuthenticationUrl())
                .successHandler(baseAuthenticationSuccessHandler)
                .failureHandler(baseAuthenticationFailureHandler)
                .failureUrl(clientProperties.getFailureUrl())
                .defaultSuccessUrl(clientProperties.getSuccessUrl())
                // 由 Spring Security 接管，不用任何处理
                .loginProcessingUrl(clientProperties.getLoginProcessingUrl())
                // 配置 uri 验证与授权信息
                .and()
                .authorizeRequests()
                .antMatchers(permitAllArray).permitAll()
                .antMatchers(denyAllArray).denyAll()
                .antMatchers(anonymousArray).anonymous()
                .antMatchers(authenticatedArray).authenticated()
                .antMatchers(fullyAuthenticatedArray).fullyAuthenticated()
                .antMatchers(rememberMeArray).rememberMe()
                .anyRequest()
                .authenticated()
                // logout
                .and()
                .logout()
                .logoutUrl(clientProperties.getLogoutUrl())
                .logoutSuccessHandler(new DefaultLogoutSuccessHandler(clientProperties, objectMapper, cacheUserDetailsService))
                .deleteCookies(clientProperties.getRememberMe().getRememberMeCookieName(),
                               clientProperties.getSession().getSessionCookieName())
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                // 配置 csrf
                .and()
                .csrf().disable();

        if (socialWebSecurityConfigurerMap != null)
        {
            for (WebSecurityConfigurerAware postConfigurer : socialWebSecurityConfigurerMap.values())
            {
                postConfigurer.postConfigure(http);
            }
        }
    }

    /**
     * 把指定 authorizeRequestType 类型的 Set 转成 Array
     * @param authorizeRequestMap   存储种 authorizeRequestType 的 map
     * @param authorizeRequestType  authorizeRequestType
     * @return  当 authorizeRequestType 对应的 Set 为 null 时, 返回 new String[0];
     */
    private String[] set2Array(Map<String, Set<String>> authorizeRequestMap, String authorizeRequestType) {
        Set<String> set = authorizeRequestMap.get(authorizeRequestType);
        if (set != null)
        {
            String[] permitAllArray;
            permitAllArray = new String[set.size()];
            set.toArray(permitAllArray);
            if (log.isDebugEnabled())
            {
                log.debug("{} = {}", authorizeRequestType, Arrays.toString(permitAllArray));
            }
            return permitAllArray;

        }
        return new String[0];
    }

    /**
     * 把从 {@link WebSecurityConfigurerAware#getAuthorizeRequestMap()} 获取的 Map 根据权限分类进行合并,
     * 把权限作为 key 与之相对应的 uriSet 作为 value, 分类放入 map, 此 map 存储在 applicationContent 时所用的 key
     * 传入参数都不能为 null.
     * @param http  HttpSecurity
     * @param targetAuthorizeRequestMap  targetAuthorizeRequestMap
     * @throws Exception  Exception
     */
    private void fillingAuthorizeRequestUris(@NonNull HttpSecurity http,
                                              @NonNull Map<String, Set<String>> targetAuthorizeRequestMap) throws Exception {
        if (this.socialWebSecurityConfigurerMap != null)
        {
            for (WebSecurityConfigurerAware configurer : this.socialWebSecurityConfigurerMap.values())
            {
                configurer.preConfigure(http);
                Map<String, Set<String>> authorizeRequestMap = configurer.getAuthorizeRequestMap();
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, permitAll);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, denyAll);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, anonymous);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, authenticated);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, fullyAuthenticated);
                groupByMap(targetAuthorizeRequestMap, authorizeRequestMap, rememberMe);
            }

            // 把 targetAuthorizeRequestMap 添加到 ServletContext
            Objects.requireNonNull(servletWebServerApplicationContext.getServletContext())
                    .setAttribute(SERVLET_CONTEXT_AUTHORIZE_REQUESTS_MAP_KEY, targetAuthorizeRequestMap);
        }
    }


    /**
     * 把 根据权限类型从 authorizeRequestMap 提取的 uriSet 添加到 map 中
     *
     * @param targetAuthorizeRequestMap 不可以为null
     * @param srcAuthorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     */
    private void groupByMap(@NonNull Map<String, Set<String>> targetAuthorizeRequestMap,
                            @Nullable Map<String, Set<String>> srcAuthorizeRequestMap,
                            @NonNull String authorizeRequestType) {

        if (srcAuthorizeRequestMap != null)
        {
            Set<String> set = srcAuthorizeRequestMap.get(authorizeRequestType);
            if (set == null)
            {
                set = new HashSet<>();
            }
            final Set<String> uriSet = set;

            targetAuthorizeRequestMap.compute(authorizeRequestType, (k, v) -> {

                if (v == null)
                {
                    v = uriSet;
                } else
                {
                    v.addAll(uriSet);
                }
                return v;
            });
        }
    }

}


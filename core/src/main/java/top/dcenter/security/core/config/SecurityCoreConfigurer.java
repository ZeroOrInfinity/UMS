package top.dcenter.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.provider.UsernamePasswordAuthenticationProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware.anonymous;
import static top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware.authenticated;
import static top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware.denyAll;
import static top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware.fullyAuthenticated;
import static top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware.permitAll;
import static top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware.rememberMe;

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

    private final BrowserProperties browserProperties;
    private final BaseAuthenticationSuccessHandler  baseAuthenticationSuccessHandler;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;


    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Map<String, SocialWebSecurityConfigurerAware> socialWebSecurityConfigurerMap;

    public SecurityCoreConfigurer(BrowserProperties browserProperties,
                                  BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                  BaseAuthenticationFailureHandler baseAuthenticationFailureHandler, UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider) {
        this.browserProperties = browserProperties;
        this.baseAuthenticationSuccessHandler = baseAuthenticationSuccessHandler;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.usernamePasswordAuthenticationProvider = usernamePasswordAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Set<String> permitAllSet = new HashSet<>();
        Set<String> denyAllSet = new HashSet<>();
        Set<String> anonymousSet = new HashSet<>();
        Set<String> authenticatedSet = new HashSet<>();
        Set<String> fullyAuthenticatedSet = new HashSet<>();
        Set<String> rememberMeSet = new HashSet<>();

        // 对所有的AuthorizeRequestUris 进行分类，放入对应的 Set
        // 对所有的问题都是一样的
        fillingAuthorizeRequestUris(http, permitAllSet, denyAllSet, anonymousSet, authenticatedSet,
                                    fullyAuthenticatedSet, rememberMeSet);

        // 将 AuthorizeRequestUriSet 转换为对应的 array
        String[] permitAllArray = set2Array(permitAllSet, permitAll);
        String[] denyAllArray = set2Array(denyAllSet, denyAll);
        String[] anonymousArray = set2Array(anonymousSet, anonymous);
        String[] authenticatedArray = set2Array(authenticatedSet, authenticated);
        String[] fullyAuthenticatedArray = set2Array(fullyAuthenticatedSet, fullyAuthenticated);
        String[] rememberMeArray = set2Array(rememberMeSet, rememberMe);

        /* 用户密码登录的 Provider, 只是对 org.springframework.security.authentication.dao.DaoAuthenticationProvider 的 copy.
         * 替换 org.springframework.security.authentication.dao.DaoAuthenticationProvider 的一个原因是:
         * 当有 IOC 容器中有多个 UserDetailsService 时, org.springframework.security.authentication.dao
         * .DaoAuthenticationProvider 会失效.
         * 如果要对前端传过来的密码进行解密,则请实现 UserDetailsPasswordService
         */
        http.authenticationProvider(this.usernamePasswordAuthenticationProvider)
            .formLogin()
                // uri 需要自己实现
                .loginPage(this.browserProperties.getLoginUnAuthenticationUrl())
                .successHandler(this.baseAuthenticationSuccessHandler)
                .failureHandler(this.baseAuthenticationFailureHandler)
                .failureUrl(this.browserProperties.getFailureUrl())
                .defaultSuccessUrl(this.browserProperties.getSuccessUrl())
                // 由 Spring Security 接管，不用任何处理
                .loginProcessingUrl(this.browserProperties.getLoginProcessingUrl())
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
                // 配置 csrf
                .and()
                .csrf().disable();

        if (this.socialWebSecurityConfigurerMap != null)
        {
            for (SocialWebSecurityConfigurerAware postConfigurer : this.socialWebSecurityConfigurerMap.values())
            {
                postConfigurer.postConfigure(http);
            }
        }
    }

    private String[] set2Array(Set<String> permitAllSet, String authorizeRequestType) {
        String[] permitAllArray;
        permitAllArray = new String[permitAllSet.size()];
        permitAllSet.toArray(permitAllArray);
        if (log.isDebugEnabled())
        {
            log.debug("{} = {}", authorizeRequestType, Arrays.toString(permitAllArray));
        }
        return permitAllArray;
    }

    /**
     * 传入参数都不能为 null
     *
     * @param http  HttpSecurity
     * @param permitAllSet  permitAllSet
     * @param denyAllSet    denyAllSet
     * @param anonymousSet  anonymousSet
     * @param authenticatedSet  authenticatedSet
     * @param fullyAuthenticatedSet fullyAuthenticatedSet
     * @param rememberMeSet rememberMeSet
     * @throws Exception  Exception
     */
    private void fillingAuthorizeRequestUris(HttpSecurity http,
                                             Set<String> permitAllSet,
                                             Set<String> denyAllSet,
                                             Set<String> anonymousSet,
                                             Set<String> authenticatedSet,
                                             Set<String> fullyAuthenticatedSet,
                                             Set<String> rememberMeSet) throws Exception {
        if (this.socialWebSecurityConfigurerMap != null)
        {
            for (SocialWebSecurityConfigurerAware configurer : this.socialWebSecurityConfigurerMap.values())
            {
                configurer.preConfigure(http);
                Map<String, Set<String>> authorizeRequestMap = configurer.getAuthorizeRequestMap();

                add2Set(permitAllSet, authorizeRequestMap, permitAll);
                add2Set(denyAllSet, authorizeRequestMap, denyAll);
                add2Set(anonymousSet, authorizeRequestMap, anonymous);
                add2Set(authenticatedSet, authorizeRequestMap, authenticated);
                add2Set(fullyAuthenticatedSet, authorizeRequestMap, fullyAuthenticated);
                add2Set(rememberMeSet, authorizeRequestMap, rememberMe);
            }
        }
        permitAllSet.addAll(addPermitAllUriSet());
    }

    private Set<String> addPermitAllUriSet() {
        Set<String> permitAllSet = new HashSet<>();

        permitAllSet.add(this.browserProperties.getLoginUnAuthenticationUrl());
        permitAllSet.add(this.browserProperties.getFailureUrl());
        permitAllSet.add(this.browserProperties.getLoginPage());
        permitAllSet.add(this.browserProperties.getSuccessUrl());
        permitAllSet.add(this.browserProperties.getErrorUrl());
        permitAllSet.add(this.browserProperties.getError4Url());
        permitAllSet.add(this.browserProperties.getError5Url());

        return permitAllSet;
    }

    /**
     * 把 根据 authorizeRequestType 从 authorizeRequestMap 提取的 uri 添加到数组中
     *
     * @param resultSet            不可以为null
     * @param authorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     */
    private void add2Set(Set<String> resultSet, Map<String, Set<String>> authorizeRequestMap,
                         String authorizeRequestType) {
        if (authorizeRequestMap != null)
        {
            Set<String> authorizeRequestSet = authorizeRequestMap.get(authorizeRequestType);
            if (authorizeRequestSet != null && !authorizeRequestSet.isEmpty())
            {
                resultSet.addAll(authorizeRequestSet);
            }
        }
    }

}


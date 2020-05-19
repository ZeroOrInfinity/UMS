package top.dcenter.security.browser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.security.browser.authentication.BrowserAuthenticationFailureHandler;
import top.dcenter.security.browser.authentication.BrowserAuthenticationSuccessHandler;
import top.dcenter.security.core.SocialWebSecurityConfigurerAware;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.service.AbstractUserDetailsService;
import top.dcenter.security.core.validate.code.ValidateCodeSecurityConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static top.dcenter.security.core.SocialWebSecurityConfigurerAware.anonymous;
import static top.dcenter.security.core.SocialWebSecurityConfigurerAware.authenticated;
import static top.dcenter.security.core.SocialWebSecurityConfigurerAware.denyAll;
import static top.dcenter.security.core.SocialWebSecurityConfigurerAware.fullyAuthenticated;
import static top.dcenter.security.core.SocialWebSecurityConfigurerAware.permitAll;
import static top.dcenter.security.core.SocialWebSecurityConfigurerAware.rememberMe;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_DATABASE_NAME_SQL;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX;

/**
 * 网页端安全相关配置
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 13:14
 * @medifiedBy zyw
 */
@SuppressWarnings("jol")
@Configuration
@Slf4j
public class BrowserSecurityConfiguration extends WebSecurityConfigurerAdapter implements InitializingBean {

    private final BrowserProperties browserProperties;
    private final BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler;
    private final BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler;
    private final ValidateCodeSecurityConfig validateCodeSecurityConfig;
    private final DataSource dataSource;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Map<String, SocialWebSecurityConfigurerAware> webSecurityPostConfigurerMap;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private AbstractUserDetailsService userDetailsService;

    public BrowserSecurityConfiguration(BrowserProperties browserProperties,
                                        BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler,
                                        BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler,
                                        ValidateCodeSecurityConfig validateCodeSecurityConfig,
                                        DataSource dataSource) {
        this.browserProperties = browserProperties;
        this.browserAuthenticationSuccessHandler = browserAuthenticationSuccessHandler;
        this.browserAuthenticationFailureHandler = browserAuthenticationFailureHandler;
        this.validateCodeSecurityConfig = validateCodeSecurityConfig;
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 的实现了添加随机 salt 算法，并且能从hash后的字符串中获取 salt 进行原始密码与hash后的密码的对比
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // TODO 自定义 remember 的持久化功能，添加持久化功能 spi 接口，实现缓存如redis或数据库jdbc通用，自定义持久化数据库表的创建，更新，删除语句，让用户更好自定义
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Set<String> permitAllSet = new HashSet<>();
        Set<String> denyAllSet = new HashSet<>();
        Set<String> anonymousSet = new HashSet<>();
        Set<String> authenticatedSet = new HashSet<>();
        Set<String> fullyAuthenticatedSet = new HashSet<>();
        Set<String> rememberMeSet = new HashSet<>();

        // 对 所有的AuthorizeRequestUris 进行分类，放入对应的 List
        fillingAuthorizeRequestUris(http, permitAllSet, denyAllSet, anonymousSet, authenticatedSet,
                                    fullyAuthenticatedSet, rememberMeSet);

        // 将 AuthorizeRequestUriSet 转换为对应的 array
        String[] permitAllArray = set2Array(permitAllSet,  permitAll);
        String[] denyAllArray = set2Array(denyAllSet,  denyAll);
        String[] anonymousArray = set2Array(anonymousSet, anonymous);
        String[] authenticatedArray = set2Array(authenticatedSet, authenticated);
        String[] fullyAuthenticatedArray = set2Array(fullyAuthenticatedSet,  fullyAuthenticated);
        String[] rememberMeArray = set2Array(rememberMeSet,  rememberMe);


        // 配置 session 策略
        if (browserProperties.getSessionNumberSetting())
        {
            // TODO Session 各种 Strategy 未配置
            http.sessionManagement()
                    // 当设置为 1 时，同个用户登录会自动踢掉上一次的登录状态。
                    .maximumSessions(browserProperties.getMaximumSessions())
                    // 同个用户达到最大 maximumSession 后，自动拒绝用户在登录
                    .maxSessionsPreventsLogin(browserProperties.getMaxSessionsPreventsLogin());
        }

        http.formLogin()
                // uri 需要自己实现
                .loginPage(browserProperties.getLoginUnAuthenticationUrl())
                .successHandler(browserAuthenticationSuccessHandler)
                .failureHandler(browserAuthenticationFailureHandler)
                .failureUrl(browserProperties.getFailureUrl())
                .defaultSuccessUrl(browserProperties.getSuccessUrl())
                // 由 Spring Security 接管，不用任何处理
                .loginProcessingUrl(browserProperties.getLoginProcessingUrl())
                // rememberMe 功能
                .and()
                .rememberMe()
                .rememberMeParameter(DEFAULT_REMEMBER_ME_NAME)
                .rememberMeCookieName(browserProperties.getRememberMeCookieName())
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(browserProperties.getRememberMeSeconds())
                .userDetailsService(userDetailsService)
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

        if (webSecurityPostConfigurerMap != null)
        {
            for (SocialWebSecurityConfigurerAware postConfigurer : webSecurityPostConfigurerMap.values())
            {
                postConfigurer.postConfigure(http);
            }
        }
    }

    private String[] set2Array(Set<String> permitAllSet, String authorizeRequestType) {
        String[] permitAllArray;
        permitAllArray = new String[permitAllSet.size()];
        permitAllSet.toArray(permitAllArray);
        log.info("{} = {}", authorizeRequestType, Arrays.toString(permitAllArray));
        return permitAllArray;
    }

    /**
     * 传入参数都不能为 null
     * @param http
     * @param permitAllSet
     * @param denyAllSet
     * @param anonymousSet
     * @param authenticatedSet
     * @param fullyAuthenticatedSet
     * @param rememberMeSet
     * @throws Exception
     */
    private void fillingAuthorizeRequestUris(HttpSecurity http,
                                             Set<String> permitAllSet,
                                             Set<String> denyAllSet,
                                             Set<String> anonymousSet,
                                             Set<String> authenticatedSet,
                                             Set<String> fullyAuthenticatedSet,
                                             Set<String> rememberMeSet) throws Exception {
        if (webSecurityPostConfigurerMap != null)
        {
            for (SocialWebSecurityConfigurerAware postConfigurer : webSecurityPostConfigurerMap.values())
            {
                postConfigurer.preConfigure(http);
                Map<String, Set<String>> authorizeRequestMap = postConfigurer.getAuthorizeRequestMap();

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

        permitAllSet.add(browserProperties.getLoginUnAuthenticationUrl());
        permitAllSet.add(browserProperties.getFailureUrl());
        permitAllSet.add(browserProperties.getLoginPage());
        permitAllSet.add(browserProperties.getSuccessUrl());
        permitAllSet.add(browserProperties.getErrorUrl());
        permitAllSet.add(browserProperties.getError4Url());
        permitAllSet.add(browserProperties.getError5Url());

        return permitAllSet;
    }

    /**
     * 把 根据 authorizeRequestType 从 authorizeRequestMap 提取的 uri 添加到数组中
     *
     * @param resultSet           不可以为null
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


    @Override
    public void afterPropertiesSet() throws Exception {

        // 如果 JdbcTokenRepositoryImpl 所需的表 persistent_logins 未创建则创建它
        JdbcTokenRepositoryImpl jdbcTokenRepository = (JdbcTokenRepositoryImpl) persistentTokenRepository();
        DataSource dataSource = jdbcTokenRepository.getDataSource();
        try (Connection connection = dataSource.getConnection())
        {
            if (connection == null)
            {
                throw new Exception("初始化 Remember-me 的 persistent_logins 用户表时发生错误");
            }
            ResultSet resultSet = connection.prepareStatement(QUERY_DATABASE_NAME_SQL).executeQuery();
            resultSet.next();
            String database = resultSet.getString(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
            if (StringUtils.isNotBlank(database))
            {
                resultSet =
                        connection.createStatement().executeQuery(browserProperties.getQueryRememberMeTableExistSql(database));
                resultSet.next();
                int tableCount = resultSet.getInt(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                if (tableCount < 1)
                {
                    connection.prepareStatement(JdbcTokenRepositoryImpl.CREATE_TABLE_SQL).executeUpdate();
                    log.info("persistent_logins 表创建成功，SQL：{}", JdbcTokenRepositoryImpl.CREATE_TABLE_SQL);
                    if (!connection.getAutoCommit())
                    {
                        connection.commit();
                    }
                }
            }
            else
            {
                throw new Exception("初始化 Remember-me 的 persistent_logins 用户表时发生错误");
            }
        }
    }

}

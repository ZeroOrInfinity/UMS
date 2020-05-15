package top.dcenter.security.browser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.security.browser.authentication.BrowserAuthenticationFailureHandler;
import top.dcenter.security.browser.authentication.BrowserAuthenticationSuccessHandler;
import top.dcenter.security.core.SocialWebSecurityConfigurerAware;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.validate.code.ValidateCodeSecurityConfig;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter implements InitializingBean {

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
    private UserDetailsService userDetailsService;

    public BrowserSecurityConfig(BrowserProperties browserProperties,
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



        List<String> permitAllList = new ArrayList<>();
        List<String> denyAllList = new ArrayList<>();
        List<String> anonymousList = new ArrayList<>();
        List<String> authenticatedList = new ArrayList<>();
        List<String> fullyAuthenticatedList = new ArrayList<>();
        List<String> rememberMeList = new ArrayList<>();

        // 对 所有的AuthorizeRequestUris 进行分类，放入对应的 List
        fillingAuthorizeRequestUris(http, permitAllList, denyAllList, anonymousList, authenticatedList, fullyAuthenticatedList, rememberMeList);

        // 将 AuthorizeRequestUriList 转换为对应的 array
        String[] permitAllArray = list2Array(permitAllList,  permitAll);
        String[] denyAllArray = list2Array(denyAllList,  denyAll);
        String[] anonymousArray = list2Array(anonymousList, anonymous);
        String[] authenticatedArray = list2Array(authenticatedList, authenticated);
        String[] fullyAuthenticatedArray = list2Array(fullyAuthenticatedList,  fullyAuthenticated);
        String[] rememberMeArray = list2Array(rememberMeList,  rememberMe);


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

    private String[] list2Array(List<String> permitAllList, String authorizeRequestType) {
        String[] permitAllArray;
        permitAllArray = new String[permitAllList.size()];
        permitAllList.toArray(permitAllArray);
        log.info("{} = {}", authorizeRequestType, Arrays.toString(permitAllArray));
        return permitAllArray;
    }

    private void fillingAuthorizeRequestUris(HttpSecurity http,
                                             List<String> permitAllList,
                                             List<String> denyAllList,
                                             List<String> anonymousList,
                                             List<String> authenticatedList,
                                             List<String> fullyAuthenticatedList,
                                             List<String> rememberMeList) throws Exception {
        if (webSecurityPostConfigurerMap != null)
        {
            for (SocialWebSecurityConfigurerAware postConfigurer : webSecurityPostConfigurerMap.values())
            {
                postConfigurer.preConfigure(http);
                Map<String, List<String>> authorizeRequestMap = postConfigurer.getAuthorizeRequestMap();

                add2List(permitAllList, authorizeRequestMap, permitAll);
                add2List(denyAllList, authorizeRequestMap, denyAll);
                add2List(anonymousList, authorizeRequestMap, anonymous);
                add2List(authenticatedList, authorizeRequestMap, authenticated);
                add2List(fullyAuthenticatedList, authorizeRequestMap, fullyAuthenticated);
                add2List(rememberMeList, authorizeRequestMap, rememberMe);
            }
        }
        permitAllList.addAll(addPermitAllUriList());
    }

    private List<String> addPermitAllUriList() {
        List<String> permitAllList = new ArrayList<>();

        permitAllList.add(browserProperties.getLoginUnAuthenticationUrl());
        permitAllList.add(browserProperties.getFailureUrl());
        permitAllList.add(browserProperties.getLoginPage());
        permitAllList.add(browserProperties.getSuccessUrl());

        return permitAllList;
    }

    /**
     * 把 根据 authorizeRequestType 从 authorizeRequestMap 提取的 uri 添加到数组中
     *
     * @param resultList           不可以为null
     * @param authorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     */
    private void add2List(@NotNull List<String> resultList, Map<String, List<String>> authorizeRequestMap,
                          @NotNull String authorizeRequestType) {
        if (authorizeRequestMap != null)
        {
            List<String> authorizeRequestList = authorizeRequestMap.get(authorizeRequestType);
            if (authorizeRequestList != null && !authorizeRequestList.isEmpty())
            {
                resultList.addAll(authorizeRequestList);
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

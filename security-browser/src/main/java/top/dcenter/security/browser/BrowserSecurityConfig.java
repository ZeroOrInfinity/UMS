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
import top.dcenter.security.core.WebSecurityPostConfigurer;
import top.dcenter.security.core.authentication.mobile.SmsCodeAuthenticationConfig;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.ValidateCodeSecurityConfig;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static top.dcenter.security.core.WebSecurityPostConfigurer.anonymous;
import static top.dcenter.security.core.WebSecurityPostConfigurer.authenticated;
import static top.dcenter.security.core.WebSecurityPostConfigurer.denyAll;
import static top.dcenter.security.core.WebSecurityPostConfigurer.fullyAuthenticated;
import static top.dcenter.security.core.WebSecurityPostConfigurer.permitAll;
import static top.dcenter.security.core.WebSecurityPostConfigurer.rememberMe;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_DATABASE_NAME_SQL;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX;

/**
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 13:14
 * @medifiedBy zyw
 */
@SuppressWarnings("jol")
@Configuration
@Slf4j
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter implements InitializingBean {

    private final ValidateCodeProperties validateCodeProperties;
    private final BrowserProperties browserProperties;
    private final BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler;
    private final BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler;
    private final ValidateCodeSecurityConfig validateCodeSecurityConfig;
    private final DataSource dataSource;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
    @Autowired(required = false)
    private Map<String, WebSecurityPostConfigurer> webSecurityPostConfigurerMap;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SmsCodeAuthenticationConfig smsCodeAuthenticationConfig;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private UserDetailsService userDetailsService;

    public BrowserSecurityConfig(ValidateCodeProperties validateCodeProperties,
                                 BrowserProperties browserProperties,
                                 BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler,
                                 BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler,
                                 ValidateCodeSecurityConfig validateCodeSecurityConfig,
                                 DataSource dataSource) {
        this.validateCodeProperties = validateCodeProperties;
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
        ValidateCodeProperties.ImageCodeProperties imageProp = validateCodeProperties.getImage();
        ValidateCodeProperties.SmsCodeProperties smsProp = validateCodeProperties.getSms();

        String[] permitAllArray = new String[0];
        String[] denyAllArray = new String[0];
        String[] anonymousArray = new String[0];
        String[] authenticatedArray = new String[0];
        String[] fullyAuthenticatedArray = new String[0];
        String[] rememberMeArray = new String[0];

        if (webSecurityPostConfigurerMap != null)
        {
            for (WebSecurityPostConfigurer postConfigurer : webSecurityPostConfigurerMap.values())
            {
                postConfigurer.preConfigure(http);
                Map<String, List<String>> authorizeRequestMap = postConfigurer.getAuthorizeRequestMap();

                permitAllArray = add2Array(null, authorizeRequestMap, permitAll);
                denyAllArray = add2Array(null, authorizeRequestMap, denyAll);
                anonymousArray = add2Array(null, authorizeRequestMap, anonymous);
                authenticatedArray = add2Array(null, authorizeRequestMap, authenticated);
                fullyAuthenticatedArray = add2Array(null, authorizeRequestMap, fullyAuthenticated);
                rememberMeArray = add2Array(null, authorizeRequestMap, rememberMe);
            }
        }

        List<String> permitAllList = addPermitAllUriList(imageProp, smsProp);
        permitAllList.addAll(Arrays.asList(permitAllArray));
        permitAllArray = add2Array(permitAllList, null, permitAll);

        // 短信验证码登录配置
        if (smsCodeAuthenticationConfig != null)
        {
            http.apply(smsCodeAuthenticationConfig);
        }

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
                .csrf().disable()
                .apply(validateCodeSecurityConfig);

        if (webSecurityPostConfigurerMap != null)
        {
            for (WebSecurityPostConfigurer postConfigurer : webSecurityPostConfigurerMap.values())
            {
                postConfigurer.postConfigure(http);
            }
        }
    }

    private List<String> addPermitAllUriList(ValidateCodeProperties.ImageCodeProperties imageProp, ValidateCodeProperties.SmsCodeProperties smsProp) {
        List<String> permitAllList = new ArrayList<>();

        permitAllList.add(browserProperties.getLoginUnAuthenticationUrl());
        permitAllList.add(browserProperties.getFailureUrl());
        permitAllList.add(browserProperties.getLoginPage());
        permitAllList.add(browserProperties.getSuccessUrl());
        permitAllList.add(DEFAULT_VALIDATE_CODE_URL_PREFIX + "/*");
        permitAllList.addAll(smsProp.getAuthUrls());
        permitAllList.addAll(imageProp.getAuthUrls());

        return permitAllList;
    }

    /**
     * 把 uriList 的所有 uri 和 根据 authorizeRequestType 从 authorizeRequestMap 提取的 uri 共同添加到同一个数组中
     *
     * @param uriList              可以为null
     * @param authorizeRequestMap  可以为 null
     * @param authorizeRequestType 不允许为 null
     * @return String[]
     */
    private String[] add2Array(List<String> uriList, Map<String, List<String>> authorizeRequestMap,
                               @NotNull String authorizeRequestType) {
        List<String> resultList = new ArrayList<>();
        if (authorizeRequestMap != null)
        {

            List<String> targetList = authorizeRequestMap.get(authorizeRequestType);
            if (targetList != null && !targetList.isEmpty())
            {
                resultList.addAll(targetList);
            }
        }
        if (uriList != null)
        {
            resultList.addAll(uriList);
        }
        String[] result = new String[resultList.size()];
        resultList.toArray(result);
        log.info("{} = {}", authorizeRequestType, Arrays.toString(result));
        return result;
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

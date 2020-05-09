package top.dcenter.security.browser;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.social.security.SpringSocialConfigurer;
import top.dcenter.security.browser.authentication.BrowserAuthenticationFailureHandler;
import top.dcenter.security.browser.authentication.BrowserAuthenticationSuccessHandler;
import top.dcenter.security.core.authentication.mobile.SmsCodeAuthenticationConfig;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.properties.SocialProperties;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.ValidateCodeSecurityConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_REMEMBER_ME_TABLE_EXIST_SQL;
import static top.dcenter.security.core.consts.SecurityConstants.RESULT_SET_COLUMN_INDEX;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/3 13:14
 */
@SuppressWarnings("jol")
@Configuration
@Slf4j
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter implements InitializingBean {

    private final ValidateCodeProperties validateCodeProperties;
    private final BrowserProperties browserProperties;
    private final SocialProperties socialProperties;
    private final BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler;
    private final BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler;
    private final ValidateCodeSecurityConfig validateCodeSecurityConfig;
    private final DataSource dataSource;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SpringSocialConfigurer socialCoreConfigurer;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SmsCodeAuthenticationConfig smsCodeAuthenticationConfig;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private UserDetailsService userDetailsService;

    public BrowserSecurityConfig(ValidateCodeProperties validateCodeProperties,
                                 BrowserProperties browserProperties,
                                 SocialProperties socialProperties,
                                 BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler,
                                 BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler,
                                 ValidateCodeSecurityConfig validateCodeSecurityConfig,
                                 DataSource dataSource) {
        this.validateCodeProperties = validateCodeProperties;
        this.browserProperties = browserProperties;
        this.socialProperties = socialProperties;
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

        List<String> passUrls = new ArrayList<>();
        passUrls.add(browserProperties.getLoginUnAuthenticationUrl());
        passUrls.add(browserProperties.getFailureUrl());
        passUrls.add(browserProperties.getLoginPage());
        passUrls.add(browserProperties.getSuccessUrl());
        passUrls.add(DEFAULT_VALIDATE_CODE_URL_PREFIX + "/*");
        passUrls.add(socialProperties.getFilterProcessesUrl() + "/*");
        passUrls.addAll(smsProp.getAuthUrls());
        passUrls.addAll(imageProp.getAuthUrls());

        String[] passUrlArray = new String[passUrls.size()];
        passUrls.toArray(passUrlArray);
        log.info("passUrlArray = {}", Arrays.toString(passUrlArray));

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
                .antMatchers(passUrlArray).permitAll()
                .anyRequest()
                .authenticated()
                // 配置 csrf
                .and()
                .csrf().disable()
                .apply(validateCodeSecurityConfig)
                .and()
                .apply(socialCoreConfigurer);

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

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 如果 JdbcTokenRepositoryImpl 所需的表 persistent_logins 未创建则创建它
        JdbcTokenRepositoryImpl jdbcTokenRepository = (JdbcTokenRepositoryImpl) persistentTokenRepository();
        DataSource dataSource = jdbcTokenRepository.getDataSource();
        Connection connection = dataSource.getConnection();
        ResultSet resultSet = connection.createStatement().executeQuery(QUERY_REMEMBER_ME_TABLE_EXIST_SQL);
        resultSet.next();
        int tableCount = resultSet.getInt(RESULT_SET_COLUMN_INDEX);
        if (tableCount < 1)
        {
            connection.prepareStatement(JdbcTokenRepositoryImpl.CREATE_TABLE_SQL).executeUpdate();
            log.info("persistent_logins 表创建成功，SQL：{}", JdbcTokenRepositoryImpl.CREATE_TABLE_SQL);
            if (!connection.getAutoCommit())
            {
                connection.commit();
            }
        }
        connection.close();
    }
}

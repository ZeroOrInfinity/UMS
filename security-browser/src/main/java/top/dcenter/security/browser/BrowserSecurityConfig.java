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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.security.browser.authentication.BrowserAuthenticationFailureHandler;
import top.dcenter.security.browser.authentication.BrowserAuthenticationSuccessHandler;
import top.dcenter.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.util.CastUtil;
import top.dcenter.security.core.validate.code.ValidateCodeFilter;
import top.dcenter.security.core.validate.code.ValidateCodeProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_REMEMBER_ME_TABLE_EXIST_SQL;
import static top.dcenter.security.core.consts.SecurityConstants.RESULT_SET_COLUMN_INDEX;
import static top.dcenter.security.core.consts.SecurityConstants.URI_SEPARATOR;

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
    private final BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler;
    private final BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler;
    private final ValidateCodeFilter validateCodeFilter;
    private final DataSource dataSource;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private UserDetailsService userDetailsService;

    public BrowserSecurityConfig(ValidateCodeProperties validateCodeProperties,
                                 BrowserProperties browserProperties,
                                 BrowserAuthenticationSuccessHandler browserAuthenticationSuccessHandler,
                                 BrowserAuthenticationFailureHandler browserAuthenticationFailureHandler,
                                 ValidateCodeFilter validateCodeFilter,
                                 DataSource dataSource) {
        this.validateCodeProperties = validateCodeProperties;
        this.browserProperties = browserProperties;
        this.browserAuthenticationSuccessHandler = browserAuthenticationSuccessHandler;
        this.browserAuthenticationFailureHandler = browserAuthenticationFailureHandler;
        this.validateCodeFilter = validateCodeFilter;
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 的实现了添加随机 salt 算法，并且能从hash后的字符串中获取 salt 进行原始密码与hash后的密码的对比
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
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
        passUrls.add(DEFAULT_VALIDATE_CODE_URL_PREFIX + "*");
        passUrls.addAll(CastUtil.string2List(smsProp.getAuthUrls(), URI_SEPARATOR));
        passUrls.addAll(CastUtil.string2List(imageProp.getAuthUrls(), URI_SEPARATOR));

        String[] passUrlArray = new String[passUrls.size()];
        passUrls.toArray(passUrlArray);
        log.info("passUrlArray = {}", Arrays.toString(passUrlArray));

        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
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
                .apply(smsCodeAuthenticationSecurityConfig);
                // 配置 session 策略
//                .sessionManagement()
                // 当设置为 1 时，同个用户登录会自动踢掉上一次的登录状态。
//                .maximumSessions(1)
                // 同个用户达到最大 maximumSession 后，自动拒绝用户在登录
//                .maxSessionsPreventsLogin(true)
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

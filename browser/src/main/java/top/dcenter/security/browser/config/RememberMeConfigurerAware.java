package top.dcenter.security.browser.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.api.service.AbstractUserDetailsService;
import top.dcenter.security.core.config.SecurityConfiguration;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_DATABASE_NAME_SQL;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX;

/**
 * RememberMe 相关配置，如果使用了 spring session 功能，就不需要 RememberMe 相关功能了
 * @author zyw
 * @version V1.0  Created by 2020/5/28 14:06
 */
//@Configuration
@AutoConfigureAfter({SecurityRememberMeConfiguration.class, SecurityConfiguration.class})
@Slf4j
public class RememberMeConfigurerAware implements WebSecurityConfigurerAware, InitializingBean {

    private final BrowserProperties browserProperties;
    private final PersistentTokenRepository persistentTokenRepository;
    private final AbstractUserDetailsService abstractUserDetailsService;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RememberMeConfigurerAware(BrowserProperties browserProperties,
                                     AbstractUserDetailsService abstractUserDetailsService,
                                     PersistentTokenRepository persistentTokenRepository, BaseAuthenticationFailureHandler baseAuthenticationFailureHandler) {
        this.browserProperties = browserProperties;
        this.persistentTokenRepository = persistentTokenRepository;
        this.abstractUserDetailsService = abstractUserDetailsService;
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {

        // 开启 rememberMe 功能
        http.rememberMe()
                .rememberMeParameter(DEFAULT_REMEMBER_ME_NAME)
                .rememberMeCookieName(this.browserProperties.getRememberMe().getRememberMeCookieName())
                .tokenRepository(this.persistentTokenRepository)
                .tokenValiditySeconds(Integer.parseInt(String.valueOf(this.browserProperties.getRememberMe().getRememberMeTimeout().getSeconds())))
                .userDetailsService(this.abstractUserDetailsService)
                .useSecureCookie(this.browserProperties.getSession().getUseSecureCookie());
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (this.persistentTokenRepository != null)
        {
            // 如果 JdbcTokenRepositoryImpl 所需的表 persistent_logins 未创建则创建它
            JdbcTokenRepositoryImpl jdbcTokenRepository = (JdbcTokenRepositoryImpl) this.persistentTokenRepository;
            DataSource dataSource = jdbcTokenRepository.getDataSource();
            if (dataSource == null)
            {
                throw new Exception("不能获取 dataSource 错误");
            }
            try (Connection connection = dataSource.getConnection())
            {
                if (connection == null)
                {
                    throw new Exception("初始化 Remember-me 的 persistent_logins 用户表时发生错误");
                }

                String database;
                try (ResultSet resultSet = connection.prepareStatement(QUERY_DATABASE_NAME_SQL).executeQuery())
                {
                    resultSet.next();
                    database = resultSet.getString(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                }

                if (StringUtils.isNotBlank(database))
                {
                    try (ResultSet resultSet = connection.createStatement().executeQuery(browserProperties.getQueryRememberMeTableExistSql(database)))
                    {
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
                }
                else
                {
                    throw new Exception("初始化 Remember-me 的 persistent_logins 用户表时发生错误");
                }
            }
        }
    }
}

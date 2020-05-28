package top.dcenter.security.browser.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware;
import top.dcenter.security.core.api.service.AbstractUserDetailsService;
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
@Configuration
@ConditionalOnMissingClass({"org.springframework.session.Session"})
@AutoConfigureAfter({BrowserSecurityConfiguration.class})
@Slf4j
public class RememberMeConfigurerAware implements SocialWebSecurityConfigurerAware, InitializingBean {

    private final BrowserProperties browserProperties;
    private final AbstractUserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;

    public RememberMeConfigurerAware(BrowserProperties browserProperties, AbstractUserDetailsService userDetailsService, PersistentTokenRepository persistentTokenRepository) {
        this.browserProperties = browserProperties;
        this.userDetailsService = userDetailsService;
        this.persistentTokenRepository = persistentTokenRepository;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // rememberMe 功能
        http.rememberMe()
            .rememberMeParameter(DEFAULT_REMEMBER_ME_NAME)
            .rememberMeCookieName(this.browserProperties.getRememberMeCookieName())
            .tokenRepository(this.persistentTokenRepository)
            .tokenValiditySeconds(this.browserProperties.getRememberMeSeconds())
            .userDetailsService(this.userDetailsService);
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
}

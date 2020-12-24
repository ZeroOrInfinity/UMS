/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.oauth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.core.api.oauth.job.RefreshTokenJob;
import top.dcenter.ums.security.core.api.oauth.repository.factory.UsersConnectionRepositoryFactory;
import top.dcenter.ums.security.core.api.oauth.repository.jdbc.UsersConnectionRepository;
import top.dcenter.ums.security.core.api.oauth.repository.jdbc.UsersConnectionTokenRepository;
import top.dcenter.ums.security.core.api.oauth.service.Auth2UserService;
import top.dcenter.ums.security.core.api.oauth.signup.ConnectionService;
import top.dcenter.ums.security.core.api.oauth.state.service.Auth2StateCoder;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.executor.config.ExecutorAutoConfiguration;
import top.dcenter.ums.security.core.oauth.job.RefreshTokenJobImpl;
import top.dcenter.ums.security.core.oauth.justauth.Auth2RequestHolder;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.properties.RepositoryProperties;
import top.dcenter.ums.security.core.oauth.repository.factory.Auth2JdbcUsersConnectionRepositoryFactory;
import top.dcenter.ums.security.core.oauth.repository.jdbc.Auth2JdbcUsersConnectionTokenRepository;
import top.dcenter.ums.security.core.oauth.service.DefaultAuth2UserServiceImpl;
import top.dcenter.ums.security.core.oauth.signup.DefaultConnectionServiceImpl;
import top.dcenter.ums.security.core.tasks.handler.RefreshAccessTokenJobHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;

import static top.dcenter.ums.security.common.consts.SecurityConstants.QUERY_DATABASE_NAME_SQL;
import static top.dcenter.ums.security.common.consts.SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX;



/**
 * OAuth2 grant flow auto configuration
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/5 21:47
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel"})
@Configuration
@AutoConfigureAfter(value = {Auth2PropertiesAutoConfiguration.class, ExecutorAutoConfiguration.class})
@ConditionalOnProperty(prefix = "ums.oauth", name = "enabled", havingValue = "true")
@Slf4j
public class Auth2AutoConfiguration implements InitializingBean {

    private final RepositoryProperties repositoryProperties;
    private final Auth2Properties auth2Properties;
    private final DataSource dataSource;

    public Auth2AutoConfiguration(RepositoryProperties repositoryProperties, Auth2Properties auth2Properties, DataSource dataSource) {
        this.repositoryProperties = repositoryProperties;
        this.auth2Properties = auth2Properties;
        this.dataSource = dataSource;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ums.oauth", name = "enable-refresh-token-job", havingValue = "true")
    public RefreshAccessTokenJobHandler refreshAccessTokenJobHandler(Auth2Properties properties) {
        return new RefreshAccessTokenJobHandler(properties.getRefreshTokenJobCron());
    }

    @Bean
    public RefreshTokenJob refreshTokenJob(UsersConnectionTokenRepository usersConnectionTokenRepository,
                                           UsersConnectionRepository usersConnectionRepository,
                                           @Qualifier("refreshTokenTaskExecutor") ExecutorService refreshTokenTaskExecutor) {
        return new RefreshTokenJobImpl(usersConnectionRepository, usersConnectionTokenRepository,
                                       auth2Properties, refreshTokenTaskExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.oauth.service.Auth2UserService")
    public Auth2UserService auth2UserService() {
        return new DefaultAuth2UserServiceImpl();
    }

    @Bean
    public JdbcTemplate auth2UserConnectionJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public UsersConnectionRepository usersConnectionRepository(UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
                                                               JdbcTemplate auth2UserConnectionJdbcTemplate,
                                                               @Qualifier("connectionTextEncryptor") TextEncryptor connectionTextEncryptor) {
        return usersConnectionRepositoryFactory.getUsersConnectionRepository(auth2UserConnectionJdbcTemplate,
                                                                             connectionTextEncryptor,
                                                                             repositoryProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.core.api.oauth.repository.jdbc.UsersConnectionTokenRepository"})
    public UsersConnectionTokenRepository usersConnectionTokenRepository(TextEncryptor connectionTextEncryptor,
                                                                         JdbcTemplate auth2UserConnectionJdbcTemplate) {
        return new Auth2JdbcUsersConnectionTokenRepository(auth2UserConnectionJdbcTemplate, connectionTextEncryptor);
    }

    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.core.api.oauth.repository.factory.UsersConnectionRepositoryFactory"})
    public UsersConnectionRepositoryFactory usersConnectionRepositoryFactory() {
        return new Auth2JdbcUsersConnectionRepositoryFactory();
    }

    @Bean
    public TextEncryptor connectionTextEncryptor(RepositoryProperties repositoryProperties) {
        return Encryptors.text(repositoryProperties.getTextEncryptorPassword(),
                               repositoryProperties.getTextEncryptorSalt());
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.oauth.signup.ConnectionService")
    public ConnectionService connectionSignUp(UmsUserDetailsService userDetailsService,
                                              UsersConnectionTokenRepository usersConnectionTokenRepository,
                                              UsersConnectionRepository usersConnectionRepository,
                                              @Autowired(required = false) Auth2StateCoder auth2StateCoder) {
        return new DefaultConnectionServiceImpl(userDetailsService, auth2Properties,
                                                usersConnectionRepository, usersConnectionTokenRepository,
                                                auth2StateCoder);
    }

    @Bean
    public Auth2RequestHolder auth2RequestHolder() {
        return Auth2RequestHolder.getInstance();
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE", "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", "ODR_OPEN_DATABASE_RESOURCE", "ODR_OPEN_DATABASE_RESOURCE"})
    @SuppressWarnings("AlibabaMethodTooLong")
    @Override
    public void afterPropertiesSet() throws Exception {

        // ====== 是否要初始化数据库 ======
        // 如果 Auth2JdbcUsersConnectionRepository, Auth2JdbcUsersConnectionTokenRepository 所需的表 user_connection, 未创建则创建它
        try (Connection connection = dataSource.getConnection())
        {
            if (connection == null)
            {
                log.error("错误: 初始化第三方登录的 {} 用户表时发生错误", repositoryProperties.getTableName());
                throw new Exception(String.format("初始化第三方登录的 %s 用户表时发生错误", repositoryProperties.getTableName()));
            }

            String database;
            try (final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DATABASE_NAME_SQL);
                 ResultSet resultSet = preparedStatement.executeQuery())
            {
                resultSet.next();
                database = resultSet.getString(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
            }

            if (StringUtils.hasText(database))
            {
                String queryUserConnectionTableExistSql = repositoryProperties.getQueryUserConnectionTableExistSql(database);

                try (final PreparedStatement preparedStatement1 = connection.prepareStatement(queryUserConnectionTableExistSql);
                     ResultSet resultSet = preparedStatement1.executeQuery())
                {
                    resultSet.next();
                    int tableCount = resultSet.getInt(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                    if (tableCount < 1)
                    {
                        String creatUserConnectionTableSql = repositoryProperties.getCreatUserConnectionTableSql();
                        try (final PreparedStatement preparedStatement = connection.prepareStatement(creatUserConnectionTableSql)) {
                            preparedStatement.executeUpdate();
                            log.info("{} 表创建成功，SQL：{}", repositoryProperties.getTableName(),
                                     creatUserConnectionTableSql);
                            if (!connection.getAutoCommit())
                            {
                                connection.commit();
                            }
                        }
                    }
                }

                String authTokenTable = "auth_token";
                String queryAuthTokenTableExistSql = "SELECT COUNT(1) FROM information_schema.tables WHERE " +
                        "table_schema='" + database + "' AND table_name = '" + authTokenTable + "'";

                //noinspection TryStatementWithMultipleResources,TryStatementWithMultipleResources
                try (final PreparedStatement preparedStatement2 = connection.prepareStatement(queryAuthTokenTableExistSql);
                     ResultSet resultSet = preparedStatement2.executeQuery())
                {
                    resultSet.next();
                    int tableCount = resultSet.getInt(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                    if (tableCount < 1)
                    {
                        String creatAuthTokenTableSql = "CREATE TABLE `" + authTokenTable + "` (\n" +
                                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                                "  `enableRefresh` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否支持 refreshToken, 默认: 1. 1 表示支持, 0 表示不支持',\n" +
                                "  `providerId` varchar(20) DEFAULT NULL COMMENT '第三方服务商,如: qq,github',\n" +
                                "  `accessToken` varchar(64) COMMENT 'accessToken',\n" +
                                "  `expireIn` bigint(20) COMMENT '过期时间',\n" +
                                "  `refreshToken` varchar(64) COMMENT 'refreshToken',\n" +
                                "  `uid` varchar(20) COMMENT 'alipay userId',\n" +
                                "  `openId` varchar(64) COMMENT 'qq/mi/toutiao/wechatMp/wechatOpen/weibo/jd/kujiale/dingTalk/douyin/feishu',\n" +
                                "  `accessCode` varchar(64) COMMENT 'dingTalk, taobao 附带属性',\n" +
                                "  `unionId` varchar(64) COMMENT 'QQ附带属性',\n" +
                                "  `scope` varchar(64) COMMENT 'Google附带属性',\n" +
                                "  `tokenType` varchar(20) COMMENT 'Google附带属性',\n" +
                                "  `idToken` varchar(64) COMMENT 'Google附带属性',\n" +
                                "  `macAlgorithm` varchar(20) COMMENT '小米附带属性',\n" +
                                "  `macKey` varchar(64) COMMENT '小米附带属性',\n" +
                                "  `code` varchar(64) COMMENT '企业微信附带属性',\n" +
                                "  `oauthToken` varchar(64) COMMENT 'Twitter附带属性',\n" +
                                "  `oauthTokenSecret` varchar(64) COMMENT 'Twitter附带属性',\n" +
                                "  `userId` varchar(64) COMMENT 'Twitter附带属性',\n" +
                                "  `screenName` varchar(64) COMMENT 'Twitter附带属性',\n" +
                                "  `oauthCallbackConfirmed` varchar(64) COMMENT 'Twitter附带属性',\n" +
                                "  `expireTime` bigint(20) DEFAULT '-1' COMMENT '过期时间, 基于 1970-01-01T00:00:00Z, 无过期时间默认为 -1',\n" +
                                "  PRIMARY KEY (`id`)\n" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
                        connection.prepareStatement(creatAuthTokenTableSql).executeUpdate();
                        log.info("{} 表创建成功，SQL：{}", authTokenTable,
                                 creatAuthTokenTableSql);
                        if (!connection.getAutoCommit())
                        {
                            connection.commit();
                        }
                    }
                }
            }
            else
            {
                log.error("错误: 初始化第三方登录的 {} 用户表时发生错误", repositoryProperties.getTableName());
                throw new Exception(String.format("初始化第三方登录的 %s 用户表时发生错误",
                                                  repositoryProperties.getTableName()));
            }
        }

    }
}
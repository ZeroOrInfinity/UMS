package top.dcenter.security.social;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

import static top.dcenter.security.core.consts.SecurityConstants.QUERY_DATABASE_NAME_SQL;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX;

/**
 * social 第三方登录通用配置
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/8 22:21
 * @medifiedBy zyw
 */
@Configuration
@EnableSocial
@EnableConfigurationProperties({SocialProperties.class})
@Slf4j
public class SocialConfiguration extends SocialConfigurerAdapter implements InitializingBean {
    private final DataSource dataSource;
    private final SocialProperties socialProperties;

    private UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;

    private final Object lockFlag = new Object();

    public SocialConfiguration(DataSource dataSource,
                               SocialProperties socialProperties) {
        this.dataSource = dataSource;
        this.socialProperties = socialProperties;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return usersConnectionRepositoryFactory().getUsersConnectionRepository(dataSource,
                                                                               connectionFactoryLocator,
                                                                               socialTextEncryptor(socialProperties),
                                                                               socialProperties,
                                                                               null,
                                                                               false);
    }

    @Bean
    public TextEncryptor socialTextEncryptor(SocialProperties socialProperties) {
        return Encryptors.text(socialProperties.getTextEncryptorPassword(),
                                                         socialProperties.getTextEncryptorSalt());
    }

    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
        return new ProviderSignInUtils(connectionFactoryLocator, getUsersConnectionRepository(connectionFactoryLocator));
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionSignUp.class)
    public ConnectionSignUp connectionSignUp() {
        return new DefaultConnectionSignUp();
    }

    @Bean
    @ConditionalOnMissingBean
    public UsersConnectionRepositoryFactory usersConnectionRepositoryFactory() {
        if (this.usersConnectionRepositoryFactory == null)
        {
            synchronized (lockFlag) {
                if (this.usersConnectionRepositoryFactory == null)
                {
                    this.usersConnectionRepositoryFactory = new OAuthJdbcUsersConnectionRepositoryFactory();
                    return this.usersConnectionRepositoryFactory;
                }
            }
        }
        return this.usersConnectionRepositoryFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public SocialCoreConfigurer socialCoreConfigurer() {
        SocialCoreConfigurer socialCoreConfigurer =
                new SocialCoreConfigurer(socialProperties);
        return socialCoreConfigurer;
    }

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Override
    public void afterPropertiesSet() throws Exception {

        // 如果 JdbcUsersConnectionRepository 所需的表 UserConnection 未创建则创建它
        try (Connection connection = dataSource.getConnection())
        {
            ResultSet resultSet = connection.prepareStatement(QUERY_DATABASE_NAME_SQL).executeQuery();
            resultSet.next();
            String database = resultSet.getString(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
            if (StringUtils.isNotBlank(database))
            {
                String queryUserConnectionTableExistSql = socialProperties.getQueryUserConnectionTableExistSql(database);
                resultSet = connection.prepareStatement(queryUserConnectionTableExistSql).executeQuery();
                resultSet.next();
                int tableCount = resultSet.getInt(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                if (tableCount < 1)
                {
                    String creatUserConnectionTableSql = socialProperties.getCreatUserConnectionTableSql();
                    connection.prepareStatement(creatUserConnectionTableSql).executeUpdate();
                    log.info("{} 表创建成功，SQL：{}", socialProperties.getTableName(),
                            creatUserConnectionTableSql);
                    if (!connection.getAutoCommit())
                    {
                        connection.commit();
                    }
                }
            }
            else
            {
                throw new Exception(String.format("初始化第三方登录的 %s 用户表时发生错误",
                                                  socialProperties.getTableName()));
            }
        }

    }


}

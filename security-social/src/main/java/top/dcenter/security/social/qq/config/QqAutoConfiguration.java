package top.dcenter.security.social.qq.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import top.dcenter.security.social.SocialProperties;
import top.dcenter.security.social.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.qq.connect.QqConnectionFactory;

import javax.sql.DataSource;

/**
 * qq 第三方登录自动配置，根据用户是否填写来绝对是否开启 QQ 登录功能<br>
 *     SocialAutoConfigurerAdapter 适用于 spring boot 1.5.x, <br>
 *     SocialConfigurerAdapter 适用于 spring boot 2.x
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 23:36
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social.qq", name = "app-id")
public class QqAutoConfiguration extends SocialConfigurerAdapter {

    private final SocialProperties socialProperties;
    private final DataSource dataSource;
    private final ConnectionSignUp connectionSignUp;
    private final UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;
    private final TextEncryptor socialTextEncryptor;


    public QqAutoConfiguration(SocialProperties socialProperties,
                               ConnectionSignUp connectionSignUp,
                               DataSource dataSource,
                               UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
                               @Qualifier("socialTextEncryptor") TextEncryptor socialTextEncryptor) {
        this.socialProperties = socialProperties;
        this.connectionSignUp = connectionSignUp;
        this.dataSource = dataSource;
        this.usersConnectionRepositoryFactory = usersConnectionRepositoryFactory;
        this.socialTextEncryptor = socialTextEncryptor;
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(this.createConnectionFactory());
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {

        return usersConnectionRepositoryFactory
                .getUsersConnectionRepository(dataSource,
                                              connectionFactoryLocator,
                                              socialTextEncryptor,
                                              socialProperties,
                                              connectionSignUp,
                                              socialProperties.getAutoSignIn());
    }


    protected ConnectionFactory<?> createConnectionFactory() {
        SocialProperties.QqProperties qq = socialProperties.getQq();
        return new QqConnectionFactory(qq.getProviderId(), qq.getAppId(), qq.getAppSecret());
    }
}

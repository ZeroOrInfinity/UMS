package top.dcenter.security.social.gitee.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.web.servlet.View;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.social.api.config.OAuth2ConfigurerAdapter;
import top.dcenter.security.social.SocialProperties;
import top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.gitee.connect.GiteeConnectionFactory;
import top.dcenter.security.social.view.ConnectView;

import javax.sql.DataSource;

/**
 * gitee 第三方登录自动配置，根据用户是否填写来绝对是否开启 gitee 登录功能<br>
 *     SocialAutoConfigurerAdapter 适用于 spring boot 1.5.x, <br>
 *     SocialConfigurerAdapter 适用于 spring boot 2.x
 * @author  zyw
 * @version V1.0  Created by 2020/5/8 23:36
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social.gitee", name = "app-id")
public class GiteeAutoConfiguration extends OAuth2ConfigurerAdapter {

    private final SocialProperties socialProperties;
    private final DataSource dataSource;
    private final ConnectionSignUp connectionSignUp;
    private final UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;
    private final TextEncryptor socialTextEncryptor;


    public GiteeAutoConfiguration(SocialProperties socialProperties,
                                  ConnectionSignUp connectionSignUp,
                                  DataSource dataSource,
                                  UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
                                  @Qualifier("socialTextEncryptor") TextEncryptor socialTextEncryptor) {
        super(socialProperties, connectionSignUp, dataSource, usersConnectionRepositoryFactory, socialTextEncryptor);
        this.socialProperties = socialProperties;
        this.connectionSignUp = connectionSignUp;
        this.dataSource = dataSource;
        this.usersConnectionRepositoryFactory = usersConnectionRepositoryFactory;
        this.socialTextEncryptor = socialTextEncryptor;
    }

    @Bean({"connect/giteeConnect", "connect/giteeConnected"})
    @ConditionalOnMissingBean(name = "giteeConnectedView")
    public View giteeConnectedView(BrowserProperties browserProperties) {
        return new ConnectView(browserProperties, objectMapper);
    }

    @Bean("gitee")
    public ConnectionFactory<?> giteeConnectionFactory() {
        SocialProperties.GiteeProperties gitee = socialProperties.getGitee();
        return new GiteeConnectionFactory(gitee.getProviderId(), gitee.getAppId(), gitee.getAppSecret(), this.objectMapper);
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(this.giteeConnectionFactory());
    }
}

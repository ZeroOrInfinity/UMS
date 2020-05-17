/**
 * 
 */
package top.dcenter.security.social.weixin.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.web.servlet.View;
import top.dcenter.security.social.SocialProperties;
import top.dcenter.security.social.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.view.ConnectView;
import top.dcenter.security.social.weixin.connect.WeixinConnectionFactory;

import javax.sql.DataSource;

/**
 * 微信登录配置
 * 
 * @author zhailiang
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social.weixin", name = "app-id")
public class WeixinAutoConfiguration extends SocialConfigurerAdapter {

	private final SocialProperties socialProperties;
	private final UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;
	private final DataSource dataSource;
	private final ConnectionSignUp connectionSignUp;
	private final TextEncryptor socialTextEncryptor;

	public WeixinAutoConfiguration(SocialProperties socialProperties,
	                               UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
	                               ConnectionSignUp connectionSignUp,
	                               DataSource dataSource,
	                               @Qualifier("socialTextEncryptor") TextEncryptor socialTextEncryptor) {
		this.socialProperties = socialProperties;
		this.usersConnectionRepositoryFactory = usersConnectionRepositoryFactory;
		this.connectionSignUp = connectionSignUp;
		this.dataSource = dataSource;
		this.socialTextEncryptor = socialTextEncryptor;
	}

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
		connectionFactoryConfigurer.addConnectionFactory(this.createConnectionFactory());
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



	@Bean({"connect/weixinConnect", "connect/weixinConnected"})
	@ConditionalOnMissingBean(name = "weixinConnectedView")
	public View weixinConnectedView() {
		return new ConnectView();
	}

	/**
	 * @see org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter
	 * #createConnectionFactory()
	 */
	protected ConnectionFactory<?> createConnectionFactory() {
		SocialProperties.WeixinProperties weixinConfig = socialProperties.getWeixin();
		return new WeixinConnectionFactory(weixinConfig.getProviderId(), weixinConfig.getAppId(),
		                                   weixinConfig.getAppSecret());
	}

}

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
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.web.servlet.View;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.social.api.config.OAuth2ConfigurerAdapter;
import top.dcenter.security.social.SocialProperties;
import top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.view.ConnectView;
import top.dcenter.security.social.weixin.connect.WeixinConnectionFactory;

import javax.sql.DataSource;

/**
 * 微信第三方登录自动配置，根据用户是否填写来绝对是否开启 微信 登录功能<br>
 *     SocialAutoConfigurerAdapter 适用于 spring boot 1.5.x, <br>
 *     SocialConfigurerAdapter 适用于 spring boot 2.x
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 23:36
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social.weixin", name = "app-id")
public class WeixinAutoConfiguration extends OAuth2ConfigurerAdapter {

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
		super(socialProperties, connectionSignUp, dataSource, usersConnectionRepositoryFactory, socialTextEncryptor);
		this.socialProperties = socialProperties;
		this.usersConnectionRepositoryFactory = usersConnectionRepositoryFactory;
		this.connectionSignUp = connectionSignUp;
		this.dataSource = dataSource;
		this.socialTextEncryptor = socialTextEncryptor;
	}

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
		connectionFactoryConfigurer.addConnectionFactory(this.weixinConnectionFactory());
	}

	@Bean({"connect/weixinConnect", "connect/weixinConnected"})
	@ConditionalOnMissingBean(name = "weixinConnectedView")
	public View weixinConnectedView(BrowserProperties browserProperties) {
		return new ConnectView(browserProperties, objectMapper);
	}

	@Bean("weixin")
	public ConnectionFactory<?> weixinConnectionFactory() {
		SocialProperties.WeixinProperties weixinConfig = socialProperties.getWeixin();
		return new WeixinConnectionFactory(weixinConfig.getProviderId(),
		                                   weixinConfig.getAppId(),
		                                   weixinConfig.getAppSecret(),
		                                   this.objectMapper);
	}

}

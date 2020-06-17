/**
 * 
 */
package top.dcenter.security.social.weixin.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionSignUp;
import top.dcenter.security.social.api.banding.ShowConnectViewService;
import top.dcenter.security.social.api.config.OAuth2ConfigurerAdapter;
import top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.view.ConnectView;
import top.dcenter.security.social.weixin.connect.WeixinConnectionFactory;

import javax.sql.DataSource;

import static top.dcenter.security.social.controller.BandingConnectController.BIND_SUFFIX;
import static top.dcenter.security.social.controller.BandingConnectController.UNBIND_SUFFIX;

/**
 * 微信第三方登录自动配置，根据用户是否填写来绝对是否开启 微信 登录功能<br><br>
 *     SocialAutoConfigurerAdapter 适用于 spring boot 1.5.x, <br><br>
 *     SocialConfigurerAdapter 适用于 spring boot 2.x
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 23:36
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social.weixin", name = "app-id")
public class WeixinAutoConfiguration extends OAuth2ConfigurerAdapter implements InitializingBean {

	@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
	@Autowired
	private GenericApplicationContext applicationContext;

	public WeixinAutoConfiguration(SocialProperties socialProperties,
	                               UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
	                               ConnectionSignUp connectionSignUp,
	                               DataSource dataSource,
	                               @Qualifier("socialTextEncryptor") TextEncryptor socialTextEncryptor) {
		super(socialProperties, connectionSignUp, dataSource, usersConnectionRepositoryFactory, socialTextEncryptor);
	}

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
		connectionFactoryConfigurer.addConnectionFactory(this.weixinConnectionFactory());
	}



	@Bean("weixin")
	public ConnectionFactory<?> weixinConnectionFactory() {
		SocialProperties.WeixinProperties weixinConfig = this.socialProperties.getWeixin();
		return new WeixinConnectionFactory(weixinConfig.getProviderId(),
		                                   weixinConfig.getAppId(),
		                                   weixinConfig.getAppSecret(),
		                                   this.objectMapper,
		                                   this.socialProperties);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// ====== 注册 ConnectView, 微信绑定与解绑后回显的页面======
		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

		// 获取 ShowConnectionStatusViewService bean
		ShowConnectViewService showConnectViewService =
				applicationContext.getBean(ShowConnectViewService.class);

		// 注册 ConnectView 到 IOC 容器
		String providerId = socialProperties.getWeixin().getProviderId();
		String viewPath = socialProperties.getViewPath();
		String connectViewBeanName = viewPath + providerId + BIND_SUFFIX;
		beanFactory.registerSingleton(connectViewBeanName,
		                              new ConnectView(showConnectViewService));

		beanFactory.registerAlias(connectViewBeanName, viewPath + providerId + UNBIND_SUFFIX);

	}
}

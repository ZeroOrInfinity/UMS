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

package top.dcenter.ums.security.social.provider.weixin.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionSignUp;
import top.dcenter.ums.security.social.api.banding.ShowConnectViewService;
import top.dcenter.ums.security.social.api.config.BaseSocialConfigurerAdapter;
import top.dcenter.ums.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.ums.security.social.config.SocialAutoConfiguration;
import top.dcenter.ums.security.social.controller.BandingConnectController;
import top.dcenter.ums.security.social.properties.SocialProperties;
import top.dcenter.ums.security.social.provider.weixin.connect.WeixinConnectionFactory;
import top.dcenter.ums.security.social.view.ConnectView;

import javax.sql.DataSource;

/**
 * 微信第三方登录自动配置，根据用户是否填写来判断是否开启 微信 登录功能<br><br>
 *     SocialAutoConfigurerAdapter 适用于 spring boot 1.5.x, <br><br>
 *     SocialConfigurerAdapter 适用于 spring boot 2.x
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/8 23:36
 */
@Configuration
@AutoConfigureAfter({SocialAutoConfiguration.class})
@ConditionalOnProperty(prefix = "ums.social.weixin", name = "enable", havingValue = "true")
public class WeixinAutoConfiguration extends BaseSocialConfigurerAdapter implements InitializingBean {

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
		return new WeixinConnectionFactory(weixinConfig.getAppId(),
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
		String connectViewBeanName = viewPath + providerId + BandingConnectController.BIND_SUFFIX;
		beanFactory.registerSingleton(connectViewBeanName,
		                              new ConnectView(showConnectViewService));

		beanFactory.registerAlias(connectViewBeanName, viewPath + providerId + BandingConnectController.UNBIND_SUFFIX);

	}
}
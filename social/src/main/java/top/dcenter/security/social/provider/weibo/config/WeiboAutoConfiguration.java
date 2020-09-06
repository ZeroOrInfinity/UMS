package top.dcenter.security.social.provider.weibo.config;

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
import top.dcenter.security.social.api.config.BaseSocialConfigurerAdapter;
import top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.provider.weibo.connect.WeiboConnectionFactory;
import top.dcenter.security.social.view.ConnectView;

import javax.sql.DataSource;

import static top.dcenter.security.social.controller.BandingConnectController.BIND_SUFFIX;
import static top.dcenter.security.social.controller.BandingConnectController.UNBIND_SUFFIX;

/**
 * weibo 第三方登录自动配置，根据用户是否填写来判断是否开启 Weibo 登录功能<br><br>
 *     SocialAutoConfigurerAdapter 适用于 spring boot 1.5.x, <br><br>
 *     SocialConfigurerAdapter 适用于 spring boot 2.x
 * @author zyw
 * @version V1.0  Created by 2020/6/18 23:36
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social.weibo", name = "app-id")
public class WeiboAutoConfiguration extends BaseSocialConfigurerAdapter implements InitializingBean {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    public WeiboAutoConfiguration(SocialProperties socialProperties,
                                  ConnectionSignUp connectionSignUp,
                                  DataSource dataSource,
                                  UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
                                  @Qualifier("socialTextEncryptor") TextEncryptor socialTextEncryptor) {
        super(socialProperties, connectionSignUp, dataSource, usersConnectionRepositoryFactory, socialTextEncryptor);
    }

    @Bean("weibo")
    public ConnectionFactory<?> weiboConnectionFactory() {
        SocialProperties.WeiboProperties weibo = this.socialProperties.getWeibo();
        return new WeiboConnectionFactory(weibo.getAppId(), weibo.getAppSecret(), this.objectMapper,
                                          this.socialProperties);
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(weiboConnectionFactory());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // ====== 注册 ConnectView, qq 绑定与解绑后回显的页面======
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // 获取 ShowConnectionStatusViewService bean
        ShowConnectViewService showConnectViewService =
                applicationContext.getBean(ShowConnectViewService.class);

        // 注册 ConnectView 到 IOC 容器
        String providerId = socialProperties.getWeibo().getProviderId();
        String viewPath = socialProperties.getViewPath();
        String connectViewBeanName = viewPath + providerId + BIND_SUFFIX;
        beanFactory.registerSingleton(connectViewBeanName,
                                      new ConnectView(showConnectViewService));

        beanFactory.registerAlias(connectViewBeanName, viewPath + providerId + UNBIND_SUFFIX);

    }
}

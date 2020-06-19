package top.dcenter.security.social.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.connect.web.DisconnectInterceptor;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.util.CollectionUtils;
import top.dcenter.security.core.properties.ClientProperties;
import top.dcenter.security.social.api.banding.ShowConnectViewService;
import top.dcenter.security.social.api.banding.ShowConnectionStatusViewService;
import top.dcenter.security.social.api.callback.RedirectUrlHelper;
import top.dcenter.security.social.api.config.SocialCoreConfig;
import top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailService;
import top.dcenter.security.social.banding.DefaultShowConnectViewService;
import top.dcenter.security.social.banding.DefaultShowConnectionStatusViewService;
import top.dcenter.security.social.controller.BandingConnectController;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.repository.jdbc.JdbcConnectionDataRepository;
import top.dcenter.security.social.repository.jdbc.factory.OAuthJdbcUsersConnectionRepositoryFactory;
import top.dcenter.security.social.signup.DefaultConnectionSignUp;
import top.dcenter.security.social.view.ConnectionStatusView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import static top.dcenter.security.core.consts.SecurityConstants.QUERY_DATABASE_NAME_SQL;
import static top.dcenter.security.core.consts.SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX;
import static top.dcenter.security.social.controller.BandingConnectController.BANDING_STATUS_SUFFIX;

/**
 * social 第三方登录通用配置
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/8 22:21
 * @author zyw
 */
@Configuration
@EnableSocial
@EnableConfigurationProperties({SocialProperties.class})
@Slf4j
public class SocialConfiguration extends SocialConfigurerAdapter implements InitializingBean {
    private final DataSource dataSource;
    private final SocialProperties socialProperties;
    private final List<ConnectInterceptor<?>> connectInterceptors;
    private final List<DisconnectInterceptor<?>> disconnectInterceptors;
    private final AbstractSocialUserDetailService userDetailService;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    private UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;

    public SocialConfiguration(ObjectProvider<List<ConnectInterceptor<?>>> connectInterceptorsProvider,
                               ObjectProvider<List<DisconnectInterceptor<?>>> disconnectInterceptorsProvider,
                               DataSource dataSource,
                               SocialProperties socialProperties,
                               AbstractSocialUserDetailService userDetailService) {
        this.dataSource = dataSource;
        this.socialProperties = socialProperties;
        this.userDetailService = userDetailService;
        this.connectInterceptors = connectInterceptorsProvider.getIfAvailable();
        this.disconnectInterceptors = disconnectInterceptorsProvider.getIfAvailable();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return this.usersConnectionRepositoryFactory.getUsersConnectionRepository(dataSource,
                                                                               connectionFactoryLocator,
                                                                               socialTextEncryptor(socialProperties),
                                                                               socialProperties,
                                                                               null,
                                                                               false);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.social.api.banding.IBandingController")
    public BandingConnectController connectController(
            ConnectionFactoryLocator factoryLocator,
            ConnectionRepository repository) {

        // repository 在创建时通过 spring 自动注入一个代理，在调用 connectionRepository的方法之前，通过
        // {@link org.springframework.aop.framework.CglibAopProxy}中的
        // {@link CglibAopProxy.DynamicAdvisedInterceptor#intercept(Object, Method, Object[], MethodProxy)} 方法
        // 注入相应的 request-scoped connectionRepository。
        // 典型用法，比如：ConnectionRepository声明@bean时，
        // 再添加一个@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
        BandingConnectController controller = new BandingConnectController(factoryLocator,
                                                                           this.socialProperties,
                                                                           repository);
        // 设置 OAuth 回调地址
        controller.setCallbackUrl(this.socialProperties.getDomain() + this.socialProperties.getCallbackUrl());
        // 设置绑定与解绑拦截器
        if (!CollectionUtils.isEmpty(this.connectInterceptors)) {
            controller.setConnectInterceptors(this.connectInterceptors);
        }
        if (!CollectionUtils.isEmpty(this.disconnectInterceptors)) {
            controller.setDisconnectInterceptors(this.disconnectInterceptors);
        }
        return controller;
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.social.api.callback.RedirectUrlHelper")
    public RedirectUrlHelper redirectUrlHelper() {
        return new RedirectUrlHelper();
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
    @ConditionalOnMissingBean(type = "org.springframework.social.connect.ConnectionSignUp")
    public ConnectionSignUp connectionSignUp() {
        return new DefaultConnectionSignUp(userDetailService);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.social.api.banding.ShowConnectViewService")
    public ShowConnectViewService showConnectViewService(ClientProperties clientProperties, ObjectMapper objectMapper) {
        return new DefaultShowConnectViewService(clientProperties, objectMapper, this.socialProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.social.api.banding.ShowConnectionStatusViewService")
    public ShowConnectionStatusViewService showConnectionStatusViewService(ObjectMapper objectMapper) {
        return new DefaultShowConnectionStatusViewService(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory")
    public UsersConnectionRepositoryFactory usersConnectionRepositoryFactory(JdbcConnectionDataRepository jdbcConnectionDataRepository) {
        OAuthJdbcUsersConnectionRepositoryFactory oAuthJdbcUsersConnectionRepositoryFactory = new OAuthJdbcUsersConnectionRepositoryFactory(jdbcConnectionDataRepository);
        this.usersConnectionRepositoryFactory = oAuthJdbcUsersConnectionRepositoryFactory;
        return oAuthJdbcUsersConnectionRepositoryFactory;
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.social.api.config.SocialCoreConfig")
    public SocialCoreConfig socialCoreConfigurer() {
        return new SocialCoreConfig(socialProperties);
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        // ====== 注册 ConnectionStatusView ======

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // 获取 ShowConnectionStatusViewService bean
        ShowConnectionStatusViewService showConnectionStatusViewService = applicationContext.getBean(ShowConnectionStatusViewService.class);

        // 注册 ConnectionStatusView 到 IOC 容器
        beanFactory.registerSingleton(socialProperties.getViewPath() + BANDING_STATUS_SUFFIX,
                                                          new ConnectionStatusView(showConnectionStatusViewService));


        // ====== 是否要初始化数据库 ======
        // 如果 JdbcUsersConnectionRepository 所需的表 UserConnection 未创建则创建它
        try (Connection connection = dataSource.getConnection())
        {
            if (connection == null)
            {
                log.error("错误: 初始化第三方登录的 {} 用户表时发生错误", socialProperties.getTableName());
                throw new Exception(String.format("初始化第三方登录的 %s 用户表时发生错误", socialProperties.getTableName()));
            }

            String database;
            try (ResultSet resultSet = connection.prepareStatement(QUERY_DATABASE_NAME_SQL).executeQuery())
            {
                resultSet.next();
                database = resultSet.getString(QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
            }

            if (StringUtils.isNotBlank(database))
            {
                String queryUserConnectionTableExistSql = socialProperties.getQueryUserConnectionTableExistSql(database);
                try (ResultSet resultSet = connection.prepareStatement(queryUserConnectionTableExistSql).executeQuery())
                {
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
            }
            else
            {
                log.error("错误: 初始化第三方登录的 {} 用户表时发生错误", socialProperties.getTableName());
                throw new Exception(String.format("初始化第三方登录的 %s 用户表时发生错误",
                                                  socialProperties.getTableName()));
            }
        }

    }

}

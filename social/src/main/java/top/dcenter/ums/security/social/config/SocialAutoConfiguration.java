package top.dcenter.ums.security.social.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
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
import org.springframework.web.context.support.GenericWebApplicationContext;
import top.dcenter.ums.security.core.properties.ClientProperties;
import top.dcenter.ums.security.core.util.MvcUtil;
import top.dcenter.ums.security.social.api.banding.IBandingController;
import top.dcenter.ums.security.social.api.banding.ShowConnectViewService;
import top.dcenter.ums.security.social.api.banding.ShowConnectionStatusViewService;
import top.dcenter.ums.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.ums.security.social.api.service.AbstractSocialUserDetailsService;
import top.dcenter.ums.security.social.banding.DefaultShowConnectViewServiceImpl;
import top.dcenter.ums.security.social.banding.DefaultShowConnectionStatusViewServiceImpl;
import top.dcenter.ums.security.social.callback.RedirectUrlHelperServiceImpl;
import top.dcenter.ums.security.social.controller.BandingConnectController;
import top.dcenter.ums.security.social.controller.SocialController;
import top.dcenter.ums.security.social.properties.SocialProperties;
import top.dcenter.ums.security.social.repository.jdbc.JdbcConnectionDataRepository;
import top.dcenter.ums.security.social.repository.jdbc.factory.OAuthJdbcUsersConnectionRepositoryFactory;
import top.dcenter.ums.security.social.signup.DefaultConnectionSignUp;
import top.dcenter.ums.security.social.view.ConnectionStatusView;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

import static top.dcenter.ums.security.core.consts.SecurityConstants.QUERY_DATABASE_NAME_SQL;
import static top.dcenter.ums.security.core.consts.SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * social 第三方登录通用配置
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/8 22:21
 * @author zyw
 */
@Configuration
@EnableSocial
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
@Slf4j
public class SocialAutoConfiguration extends SocialConfigurerAdapter implements InitializingBean {
    private final DataSource dataSource;
    private final SocialProperties socialProperties;
    private final List<ConnectInterceptor<?>> connectInterceptors;
    private final List<DisconnectInterceptor<?>> disconnectInterceptors;
    private final AbstractSocialUserDetailsService userDetailService;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    private UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;

    public SocialAutoConfiguration(ObjectProvider<List<ConnectInterceptor<?>>> connectInterceptorsProvider,
                                   ObjectProvider<List<DisconnectInterceptor<?>>> disconnectInterceptorsProvider,
                                   DataSource dataSource,
                                   SocialProperties socialProperties,
                                   AbstractSocialUserDetailsService userDetailService) {
        this.dataSource = dataSource;
        this.socialProperties = socialProperties;
        this.userDetailService = userDetailService;
        this.connectInterceptors = connectInterceptorsProvider.getIfAvailable();
        this.disconnectInterceptors = disconnectInterceptorsProvider.getIfAvailable();

    }

    @PostConstruct
    public void init() {
        // 获取 servletContextPath
        String contextPath;
        try {
            contextPath = Objects.requireNonNull(((AnnotationConfigServletWebServerApplicationContext) this.applicationContext).getServletContext()).getContextPath();
        }
        catch (Exception e) {
            contextPath = Objects.requireNonNull(((GenericWebApplicationContext) this.applicationContext).getServletContext()).getContextPath();
        }

        String callbackUrl = socialProperties.getCallbackUrl();
        String domain = socialProperties.getDomain();

        String partRedirectUrl = domain + contextPath + callbackUrl + URL_SEPARATOR;

        SocialProperties.QqProperties qq = socialProperties.getQq();
        SocialProperties.WeiboProperties weibo = socialProperties.getWeibo();
        SocialProperties.WeixinProperties weixin = socialProperties.getWeixin();
        SocialProperties.GiteeProperties gitee = socialProperties.getGitee();

        // OAuth2 回调地址中添加 servletContextPath
        qq.setRedirectUrl(partRedirectUrl + qq.getProviderId());
        weibo.setRedirectUrl(partRedirectUrl + weibo.getProviderId());
        weixin.setRedirectUrl(partRedirectUrl + weixin.getProviderId());
        gitee.setRedirectUrl(partRedirectUrl + gitee.getProviderId());

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
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.social.api.banding.IBandingController")
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
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.social.api.callback.RedirectUrlHelperService")
    public RedirectUrlHelperServiceImpl redirectUrlHelper() {
        return new RedirectUrlHelperServiceImpl();
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
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.social.api.banding.ShowConnectViewService")
    public ShowConnectViewService showConnectViewService(ClientProperties clientProperties, ObjectMapper objectMapper) {
        return new DefaultShowConnectViewServiceImpl(clientProperties, objectMapper, this.socialProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.social.api.banding.ShowConnectionStatusViewService")
    public ShowConnectionStatusViewService showConnectionStatusViewService(ObjectMapper objectMapper) {
        return new DefaultShowConnectionStatusViewServiceImpl(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.social.api.repository.UsersConnectionRepositoryFactory")
    public UsersConnectionRepositoryFactory usersConnectionRepositoryFactory(JdbcConnectionDataRepository jdbcConnectionDataRepository) {
        OAuthJdbcUsersConnectionRepositoryFactory oAuthJdbcUsersConnectionRepositoryFactory = new OAuthJdbcUsersConnectionRepositoryFactory(jdbcConnectionDataRepository);
        this.usersConnectionRepositoryFactory = oAuthJdbcUsersConnectionRepositoryFactory;
        return oAuthJdbcUsersConnectionRepositoryFactory;
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.social.config.SocialCoreConfig")
    public SocialCoreConfig socialCoreConfigurer() {
        return new SocialCoreConfig(socialProperties);
    }

    @Bean
    public JdbcConnectionDataRepository jdbcConnectionDataRepository(JdbcTemplate jdbcTemplate, TextEncryptor textEncryptor) {
        return new JdbcConnectionDataRepository(jdbcTemplate, textEncryptor, socialProperties);
    }

    @Bean
    public SocialController socialController(RedirectUrlHelperServiceImpl redirectUrlHelper) {
        return new SocialController(redirectUrlHelper);
    }


    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"OBL_UNSATISFIED_OBLIGATION", "ODR_OPEN_DATABASE_RESOURCE"})
    @Override
    public void afterPropertiesSet() throws Exception {

        // 在 mvc 中做 Uri 映射等动作
        MvcUtil.registerController("socialController", applicationContext, null);
        MvcUtil.registerController("connectController", applicationContext, IBandingController.class);

        // ====== 注册 ConnectionStatusView ======

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // 获取 ShowConnectionStatusViewService bean
        ShowConnectionStatusViewService showConnectionStatusViewService = applicationContext.getBean(ShowConnectionStatusViewService.class);

        // 注册 ConnectionStatusView 到 IOC 容器
        beanFactory.registerSingleton(socialProperties.getViewPath() + BandingConnectController.BANDING_STATUS_SUFFIX,
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

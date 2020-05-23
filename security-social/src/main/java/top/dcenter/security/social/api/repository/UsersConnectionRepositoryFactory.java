package top.dcenter.security.social.api.repository;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import top.dcenter.security.social.SocialProperties;

import javax.sql.DataSource;

/**
 * JdbcConnectionRepository 工厂
 * 用户需要对第三方登录的用户表更改时，要实现此功能。
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/13 23:04
 */
public interface UsersConnectionRepositoryFactory {
    /**
     * UsersConnectionRepository 工厂
     * @param dataSource
     * @param connectionFactoryLocator
     * @param textEncryptor     对 key 与 secret 进行加解密。
     * @param socialProperties
     * @param connectionSignUp 用于第三方登录自动注册为用户功能的开关，共有两个, 这俩个条件同时满足时才有效（另一个是 autoSignIn）：<br>
     *                         当传 null 值时关闭自动注册，当不为 null 且 autoSignIn=true 时开启自动注册功能，需自己实现 ConnectionSignUp，
     *                         {@link ConnectionSignUp#execute(Connection)} 从第三方的 connection 中获取用户唯一标识。<br>
     *
     * @param autoSignIn     当传 false 时关闭自动注册，当为 true 且 connectionSignUp 不为 null 时开启自动注册功能，<br>
     *                       通过配置 security.social.{providerId}.autoSignIn=true，默认为 false，可以从socialProperties获取。
     * @return
     */
    UsersConnectionRepository getUsersConnectionRepository(DataSource dataSource,
                                                           ConnectionFactoryLocator connectionFactoryLocator,
                                                           TextEncryptor textEncryptor,
                                                           SocialProperties socialProperties,
                                                           ConnectionSignUp connectionSignUp,
                                                           Boolean autoSignIn);
}

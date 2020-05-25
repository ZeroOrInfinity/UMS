package top.dcenter.security.social.api.repository;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import top.dcenter.security.social.OAuthJdbcUsersConnectionRepository;
import top.dcenter.security.social.SocialProperties;

import javax.sql.DataSource;

/**
 * UsersConnectionRepositoryFactory 接口实现，
 * 用户需要对第三方{@link SocialProperties} 字段 <i>tableName</i> 用户表更改时，要实现此接口
 * {@link UsersConnectionRepositoryFactory}
 * .<br>
 *     自定义的接口实现并注入 IOC 容器会自动覆盖此类
 * @author zyw
 * @version V1.0  Created by 2020/5/13 23:37
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuthJdbcUsersConnectionRepositoryFactory implements UsersConnectionRepositoryFactory {

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(DataSource dataSource,
                                                                  ConnectionFactoryLocator connectionFactoryLocator,
                                                                  TextEncryptor textEncryptor,
                                                                  SocialProperties socialProperties,
                                                                  ConnectionSignUp connectionSignUp,
                                                                  Boolean autoSignIn) {
        OAuthJdbcUsersConnectionRepository usersConnectionRepository =
                new OAuthJdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, textEncryptor, socialProperties);
        // 用于第三方登录自动注册为用户功能的开关，当传 null 值时关闭自动注册，当不为 null 且 autoSignIn=true 时开启自动注册功能。
        // 需自己实现 ConnectionSignUp ，功能为从第三方的 connection 中获取用户唯一标识。
        if (autoSignIn && connectionSignUp != null)
        {
            usersConnectionRepository.setConnectionSignUp(connectionSignUp);
        }
        return usersConnectionRepository;
    }
}

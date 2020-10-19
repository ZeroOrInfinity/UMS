package top.dcenter.ums.security.core.oauth.repository.factory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import top.dcenter.ums.security.core.oauth.repository.UsersConnectionRepository;
import top.dcenter.ums.security.core.oauth.repository.jdbc.Auth2JdbcUsersConnectionRepository;
import top.dcenter.ums.security.core.oauth.properties.RepositoryProperties;

/**
 * UsersConnectionRepositoryFactory 接口实现，
 * 用户需要对第三方{@link RepositoryProperties} <i>tableName</i> 用户表更改或者更改Repository的实现方式（如更换Redis）时，要实现此接口
 * {@link UsersConnectionRepositoryFactory}
 * .<br><br>
 *     自定义的接口实现并注入 IOC 容器会自动覆盖此类
 * @author zyw
 * @version V2.0  Created by 2020/5/13 23:37
 */
public class Auth2JdbcUsersConnectionRepositoryFactory implements UsersConnectionRepositoryFactory {


    @Override
    public UsersConnectionRepository getUsersConnectionRepository(JdbcTemplate auth2UserConnectionJdbcTemplate,
                                                                  TextEncryptor textEncryptor,
                                                                  RepositoryProperties repositoryProperties) {
        return new Auth2JdbcUsersConnectionRepository(auth2UserConnectionJdbcTemplate, textEncryptor, repositoryProperties);

    }
}

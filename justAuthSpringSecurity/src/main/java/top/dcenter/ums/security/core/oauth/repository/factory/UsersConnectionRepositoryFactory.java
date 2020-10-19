package top.dcenter.ums.security.core.oauth.repository.factory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import top.dcenter.ums.security.core.oauth.repository.UsersConnectionRepository;
import top.dcenter.ums.security.core.oauth.properties.RepositoryProperties;

/**
 * UsersConnectionRepository 工厂
 * @author zyw
 * @version V2.0 Created by 2020/5/13 23:04
 */
public interface UsersConnectionRepositoryFactory {
    /**
     * UsersConnectionRepository 工厂
     * @param textEncryptor                     对 key 与 secret 进行加解密。
     * @param auth2UserConnectionJdbcTemplate   对 key 与 secret 进行加解密。
     * @param repositoryProperties              repositoryProperties
     * @return  UsersConnectionRepository
     */
    UsersConnectionRepository getUsersConnectionRepository(JdbcTemplate auth2UserConnectionJdbcTemplate,
                                                           TextEncryptor textEncryptor,
                                                           RepositoryProperties repositoryProperties);
}
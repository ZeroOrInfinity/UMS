package top.dcenter.security.social.repository.jdbc;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import top.dcenter.security.social.properties.SocialProperties;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static top.dcenter.security.social.config.RedisCacheConfig.USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME;

/**
 * {@link JdbcUsersConnectionRepository}  的扩展版本, 各个方法的实现逻辑都一样， 只是抽取了 sql 语句，与 用户表的字段名称到 {@link SocialProperties},
 * 更便于用户自定义。<br><br>
 * {@link UsersConnectionRepository} that uses the JDBC API to persist connection data to a relational database.
 * The supporting schema is defined in JdbcUsersConnectionRepository.sql.
 * @author Keith Donald
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/13 13:41
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@CacheConfig(cacheManager = "socialRedisHashCacheManager")
public class OAuthJdbcUsersConnectionRepository implements UsersConnectionRepository {


    private final JdbcTemplate jdbcTemplate;

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final TextEncryptor textEncryptor;

    private ConnectionSignUp connectionSignUp;

    private SocialProperties socialProperties;

    private JdbcConnectionDataRepository jdbcConnectionDataRepository;

    public OAuthJdbcUsersConnectionRepository(DataSource dataSource,
                                              ConnectionFactoryLocator connectionFactoryLocator,
                                              TextEncryptor textEncryptor,
                                              JdbcConnectionDataRepository jdbcConnectionDataRepository,
                                              SocialProperties socialProperties) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
        this.socialProperties = socialProperties;
        this.jdbcConnectionDataRepository = jdbcConnectionDataRepository;
    }

    /**
     * The command to execute to create a new local user profile in the event no user id could be mapped to a connection.
     * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
     * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
     * @param connectionSignUp a {@link ConnectionSignUp} object
     * @see #findUserIdsWithConnection(Connection)
     */
    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }

    @Cacheable(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME, key = "#connection.key.providerId + '__' + #connection.key" +
            ".providerUserId", unless = "#result.size() == null || #result.size() == 0")
    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<String> localUserIds = jdbcTemplate.queryForList(socialProperties.getFindUserIdsWithConnectionSql(),
                                                              String.class,
                                                              key.getProviderId(),
                                                              key.getProviderUserId());
        // 自动注册代码
        if (localUserIds.size() == 0 && connectionSignUp != null) {
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null)
            {
                createConnectionRepository(newUserId).addConnection(connection);
                return List.of(newUserId);
            }
        }

        return localUserIds;
    }

    @Cacheable(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME, key = "#providerId + '__' + #providerUserIds",
            unless = "#result.size() == null || #result.size() == 0")
    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(socialProperties.getProviderIdColumnName(), providerId);
        parameters.addValue(socialProperties.getProviderUserIdColumnName(), providerUserIds);
        final Set<String> localUserIds = new HashSet<String>();
        return new NamedParameterJdbcTemplate(jdbcTemplate).query(socialProperties.getFindUserIdsConnectedToSql(),
                                                                  parameters,
                                                                  rs ->
                                                                  {
                                                                      while (rs.next()) {
                                                                          localUserIds.add(rs.getString(socialProperties.getUserIdColumnName()));
                                                                      }
                                                                      return localUserIds;
                                                                  });
    }


    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new JdbcConnectionRepository(userId, jdbcTemplate, connectionFactoryLocator, textEncryptor,
                                            socialProperties, jdbcConnectionDataRepository);
    }

}

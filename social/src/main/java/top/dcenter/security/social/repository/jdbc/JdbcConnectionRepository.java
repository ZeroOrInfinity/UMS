package top.dcenter.security.social.repository.jdbc;

import lombok.Getter;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.repository.jdbc.dto.ConnectionDataDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static top.dcenter.security.social.config.RedisCacheAutoConfig.USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME;
import static top.dcenter.security.social.config.RedisCacheAutoConfig.USER_CONNECTION_HASH_CACHE_NAME;

/**
 * {@link org.springframework.social.connect.jdbc.JdbcConnectionRepository}  的扩展版本, 各个方法的实现逻辑都一样， 只是抽取了 sql 语句，与 用户表的字段名称到 {@link SocialProperties},
 *  更便于用户自定义; 增加了 redis 缓存功能.
 * @see org.springframework.social.connect.jdbc.JdbcConnectionRepository
 * @author Keith Donald
 * @author zyw
 */
@SuppressWarnings({"JavadocReference"})
@CacheConfig(cacheManager = "socialRedisHashCacheManager")
public class JdbcConnectionRepository implements ConnectionRepository {

	@Getter
	private final String userId;
	
	private final JdbcTemplate jdbcTemplate;
	@Getter
	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final TextEncryptor textEncryptor;

	private final SocialProperties socialProperties;

	private final JdbcConnectionDataRepository jdbcConnectionDataRepository;

	public JdbcConnectionRepository(String userId,
	                                JdbcTemplate jdbcTemplate,
	                                ConnectionFactoryLocator connectionFactoryLocator,
	                                TextEncryptor textEncryptor,
	                                SocialProperties socialProperties,
	                                JdbcConnectionDataRepository jdbcConnectionDataRepository) {
		this.userId = userId;
		this.jdbcTemplate = jdbcTemplate;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
		this.socialProperties = socialProperties;
		this.jdbcConnectionDataRepository = jdbcConnectionDataRepository;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		return getConnectionMap(jdbcConnectionDataRepository.findAllConnections(userId));
	}

	@Override
	public List<Connection<?>> findConnections(String providerId) {
		return getConnectionList(jdbcConnectionDataRepository.findConnections(userId, providerId));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}

	@Override
	public MultiValueMap<String, Connection<?>>
	findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
		if (providerUsers == null || providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}
		StringBuilder providerUsersCriteriaSql = new StringBuilder();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(socialProperties.getUserIdColumnName(), userId);
		for (Iterator<Entry<String, List<String>>> it = providerUsers.entrySet().iterator(); it.hasNext();) {
			Entry<String, List<String>> entry = it.next();
			String providerId = entry.getKey();
			providerUsersCriteriaSql
					.append(String.format("%s = :providerId_", socialProperties.getProviderIdColumnName()))
					.append(providerId)
					.append(String.format(" and %s in (:providerUserIds_", socialProperties.getProviderUserIdColumnName()))
					.append(providerId)
					.append(")");
			parameters.addValue(String.format("%s_%s", socialProperties.getProviderIdColumnName(), providerId), providerId);
			parameters.addValue(String.format("%s_%s", socialProperties.getProviderUserIdColumnName(), providerId), entry.getValue());
			if (it.hasNext()) {
				providerUsersCriteriaSql.append(" or " );
			}
		}
		return getConnectionMap(jdbcConnectionDataRepository
				                        .findConnectionsToUsers(parameters, providerUsersCriteriaSql.toString(), userId),
		                        providerUsers);

	}

	@Override
	public Connection<?> getConnection(ConnectionKey connectionKey) {
		try {
			return getConnection(toConnectionData(jdbcConnectionDataRepository.getConnection(userId, connectionKey)));
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		if (connection == null) {
			throw new NotConnectedException(providerId);
		}
		return connection;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}

	@Caching(
		evict = {@CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
							 keyGenerator = "userIdKeyGenerator", beforeInvocation = true),
				 @CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
						     key = "#connection.createData().providerId", beforeInvocation = true),
				 @CacheEvict(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
						     keyGenerator = "addConnectionByProviderIdKeyGenerator", beforeInvocation = true)
		}
	)
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void addConnection(Connection<?> connection) {
		try {
			ConnectionData data = connection.createData();
			int rank = jdbcTemplate.queryForObject(socialProperties.getAddConnectionQueryForRankSql(),
			                                       new Object[]{ userId, data.getProviderId() }, Integer.class);
			jdbcTemplate.update(socialProperties.getAddConnectionSql(),
								userId, data.getProviderId(), data.getProviderUserId(),
					            rank, data.getDisplayName(), data.getProfileUrl(),
					            data.getImageUrl(), encrypt(data.getAccessToken()),
					            encrypt(data.getSecret()), encrypt(data.getRefreshToken()),
					            data.getExpireTime());
		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}

	@Caching(
			evict = {@CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
							keyGenerator = "userIdKeyGenerator", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
							key = "#connection.createData().providerId", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
							keyGenerator = "updateConnectionByProviderIdKeyGenerator", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
							keyGenerator = "updateConnectionByProviderIdAndProviderUserIdKeyGenerator",
							beforeInvocation = true)
			}
	)
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void updateConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();
		jdbcTemplate.update(socialProperties.getUpdateConnectionSql(),
							data.getDisplayName(), data.getProfileUrl(),
				            data.getImageUrl(), encrypt(data.getAccessToken()),
				            encrypt(data.getSecret()), encrypt(data.getRefreshToken()),
				            data.getExpireTime(), userId, data.getProviderId(),
				            data.getProviderUserId());
	}

	@Caching(
			evict = {@CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
					keyGenerator = "userIdKeyGenerator", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
							key = "#providerId", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
							keyGenerator = "removeConnectionsByProviderIdKeyGenerator", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
							keyGenerator = "removeConnectionsByUserIdAndProviderIdKeyGenerator", beforeInvocation =	true)

			}
	)
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void removeConnections(String providerId) {
		jdbcTemplate.update(socialProperties.getRemoveConnectionsSql(),
		                    userId, providerId);
	}

	@Caching(
			evict = {@CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
					keyGenerator = "userIdKeyGenerator", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME,
							key = "#connectionKey.providerId", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
							keyGenerator = "removeConnectionsByConnectionKeyKeyGenerator", beforeInvocation = true),
					@CacheEvict(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
							keyGenerator = "removeConnectionsByConnectionKeyWithProviderUserIdKeyGenerator", beforeInvocation =	true)
			}
	)
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void removeConnection(ConnectionKey connectionKey) {
		jdbcTemplate.update(socialProperties.getRemoveConnectionSql(),
		                    userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
	}


	private ConnectionData toConnectionData(ConnectionDataDTO dto) {
		return new ConnectionData(dto.getProviderId(),
		                          dto.getProviderUserId(),
		                          dto.getDisplayName(),
		                          dto.getProfileUrl(),
		                          dto.getImageUrl(),
		                          dto.getAccessToken(),
		                          dto.getSecret(),
		                          dto.getRefreshToken(),
		                          dto.getExpireTime());
	}

	private Connection<?> findPrimaryConnection(String providerId) {
		ConnectionData connectionData = toConnectionData(jdbcConnectionDataRepository.findPrimaryConnection(userId, providerId));
		if (connectionData == null) {
			return null;
		}
		return getConnection(connectionData);
	}
	
	private Connection<?> getConnection(ConnectionData connectionData) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
		return connectionFactory.createConnection(connectionData);
	}

	private List<Connection<?>> getConnectionList(List<ConnectionDataDTO> dtoList) {
		return dtoList
				.stream()
				.map(this::toConnectionData)
				.map(this::getConnection)
				.collect(Collectors.toList());
	}

	private MultiValueMap<String, Connection<?>> getConnectionMap(List<ConnectionDataDTO> connectionDataList) {
		List<Connection<?>> connectionList = getConnectionList(connectionDataList);
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.emptyList());
		}
		for (Connection<?> connection : connectionList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<>());
			}
			connections.add(providerId, connection);
		}
		return connections;

	}

	private MultiValueMap<String, Connection<?>> getConnectionMap(List<ConnectionDataDTO> connectionDataList,
	                                                                    MultiValueMap<String, String> providerUsers) {
		List<Connection<?>> connectionList = getConnectionList(connectionDataList);
		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<>();
		for (Connection<?> connection : connectionList) {
			String providerId = connection.getKey().getProviderId();
			List<String> userIds = providerUsers.get(providerId);
			List<Connection<?>> connections = connectionsForUsers.get(providerId);
			if (connections == null) {
				connections = new ArrayList<>(userIds.size());
				for (int i = 0; i < userIds.size(); i++) {
					connections.add(null);
				}
				connectionsForUsers.put(providerId, connections);
			}
			String providerUserId = connection.getKey().getProviderUserId();
			int connectionIndex = userIds.indexOf(providerUserId);
			connections.set(connectionIndex, connection);
		}
		return connectionsForUsers;

	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : null;
	}

}
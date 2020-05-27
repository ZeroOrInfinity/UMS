package top.dcenter.security.social.repository.jdbc;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * {@link org.springframework.social.connect.jdbc.JdbcConnectionRepository}  的扩展版本, 各个方法的实现逻辑都一样， 只是抽取了 sql 语句，与 用户表的字段名称到 {@link SocialProperties},
 *  更便于用户自定义。
 * @see org.springframework.social.connect.jdbc.JdbcConnectionRepository
 */
@SuppressWarnings({"JavadocReference"})
class JdbcConnectionRepository implements ConnectionRepository {

	private final String userId;
	
	private final JdbcTemplate jdbcTemplate;
	
	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final TextEncryptor textEncryptor;

	private final SocialProperties socialProperties;

	public JdbcConnectionRepository(String userId, JdbcTemplate jdbcTemplate, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor, SocialProperties socialProperties) {
		this.userId = userId;
		this.jdbcTemplate = jdbcTemplate;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
		this.socialProperties = socialProperties;
	}
	
	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		List<Connection<?>> resultList = jdbcTemplate.query(String.format("%s where %s = ? order by %s, %s",
		                                                                  socialProperties.getSelectFromUserConnectionSql(),
		                                                                  socialProperties.getUserIdColumnName(),
		                                                                  socialProperties.getProviderIdColumnName(),
		                                                                  socialProperties.getRankColumnName()),
		                                                    connectionMapper, userId);
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.emptyList());
		}
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	@Override
	public List<Connection<?>> findConnections(String providerId) {
		return jdbcTemplate.query(String.format("%s where %s = ? and %s = ? order by %s",
		                                        socialProperties.getSelectFromUserConnectionSql(),
		                                        socialProperties.getUserIdColumnName(),
		                                        socialProperties.getProviderIdColumnName(),
		                                        socialProperties.getRankColumnName()),
		                          connectionMapper, userId, providerId);
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
		List<Connection<?>> resultList = new NamedParameterJdbcTemplate(jdbcTemplate)
				.query(String.format("%s where %s = :userId and %s order by %s, %s",
                                          socialProperties.getSelectFromUserConnectionSql(),
                                          socialProperties.getUserIdColumnName(),
                                          providerUsersCriteriaSql,
                                          socialProperties.getProviderIdColumnName(),
                                          socialProperties.getRankColumnName()),
                            parameters, connectionMapper);
		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<>();
		for (Connection<?> connection : resultList) {
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

	@Override
	public Connection<?> getConnection(ConnectionKey connectionKey) {
		try {
			return jdbcTemplate.queryForObject(String.format("%s where %s = ? and %s = ? and %s = ?",
			                                                 socialProperties.getSelectFromUserConnectionSql(),
			                                                 socialProperties.getUserIdColumnName(),
			                                                 socialProperties.getProviderIdColumnName(),
			                                                 socialProperties.getProviderUserIdColumnName()),
			                                   connectionMapper, userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
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

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void removeConnections(String providerId) {
		jdbcTemplate.update(socialProperties.getRemoveConnectionsSql(),
		                    userId, providerId);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void removeConnection(ConnectionKey connectionKey) {
		jdbcTemplate.update(socialProperties.getRemoveConnectionSql(),
		                    userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
	}


	private Connection<?> findPrimaryConnection(String providerId) {
		List<Connection<?>> connections = jdbcTemplate.query(String.format("%s where %s = ? and %s = ? order by %s",
		                                                                   socialProperties.getSelectFromUserConnectionSql(),
		                                                                   socialProperties.getUserIdColumnName(),
		                                                                   socialProperties.getProviderIdColumnName(),
		                                                                   socialProperties.getRankColumnName()),
		                                                     connectionMapper,
		                                                     userId, providerId);
		if (connections.size() > 0) {
			return connections.get(0);
		} else {
			return null;
		}		
	}
	
	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	
	private final class ServiceProviderConnectionMapper implements RowMapper<Connection<?>> {
		
		@Override
		public Connection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			ConnectionData connectionData = mapConnectionData(rs);
			ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}
		
		private ConnectionData mapConnectionData(ResultSet rs) throws SQLException {
			return new ConnectionData(rs.getString(socialProperties.getProviderIdColumnName()),
			                          rs.getString(socialProperties.getProviderUserIdColumnName()),
			                          rs.getString(socialProperties.getDisplayNameColumnName()),
			                          rs.getString(socialProperties.getProfileUrlColumnName()),
			                          rs.getString(socialProperties.getImageUrlColumnName()),
			                          decrypt(rs.getString(socialProperties.getAccessTokenColumnName())),
			                          decrypt(rs.getString(socialProperties.getSecretColumnName())),
			                          decrypt(rs.getString(socialProperties.getRefreshTokenColumnName())),
			                          expireTime(rs.getLong(socialProperties.getExpireTimeColumnName())));
		}
		
		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : null;
		}
		
		private Long expireTime(long expireTime) {
			return expireTime == 0 ? null : expireTime;
		}
		
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}
	
	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : null;
	}

}
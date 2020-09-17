package top.dcenter.security.social.repository.jdbc;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionKey;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.repository.jdbc.dto.ConnectionDataDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static top.dcenter.security.social.config.RedisCacheAutoConfig.USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME;
import static top.dcenter.security.social.config.RedisCacheAutoConfig.USER_CONNECTION_HASH_CACHE_NAME;

/**
 * JdbcConnectionDataRepository
 * @author zyw
 * @version V1.0  Created by 2020/6/13 14:15
 */
@CacheConfig(cacheManager = "socialRedisHashCacheManager")
public class JdbcConnectionDataRepository {

	private JdbcTemplate jdbcTemplate;

	private TextEncryptor textEncryptor;

	private SocialProperties socialProperties;

	public JdbcConnectionDataRepository(JdbcTemplate jdbcTemplate,
	                                    TextEncryptor textEncryptor,
	                                    SocialProperties socialProperties) {
		this.jdbcTemplate = jdbcTemplate;
		this.textEncryptor = textEncryptor;
		this.socialProperties = socialProperties;
	}


	@Cacheable(cacheNames = USER_CONNECTION_HASH_CACHE_NAME, key = "#userId + '__' + #providerId", unless = "#result == null")
	public ConnectionDataDTO findPrimaryConnection(String userId, String providerId) {
		List<ConnectionDataDTO> connectionDatumDTOS = jdbcTemplate.query(String.format("%s where %s = ? and %s = ? order by %s",
		                                                                               socialProperties.getSelectFromUserConnectionSql(),
		                                                                               socialProperties.getUserIdColumnName(),
		                                                                               socialProperties.getProviderIdColumnName(),
		                                                                               socialProperties.getRankColumnName()),
		                                                                 connectionDataMapper,
		                                                                 userId, providerId);
		if (connectionDatumDTOS.size() > 0) {
			return connectionDatumDTOS.get(0);
		} else {
			return null;
		}
	}


	@Cacheable(cacheNames = USER_CONNECTION_HASH_CACHE_NAME,
			key = "#userId + ':' + #connectionKey.providerId + '__' + #connectionKey.providerUserId", unless = "#result == null")
	public ConnectionDataDTO getConnection(String userId, ConnectionKey connectionKey) throws EmptyResultDataAccessException {
		return jdbcTemplate.queryForObject(String.format("%s where %s = ? and %s = ? and %s = ?",
		                                                 socialProperties.getSelectFromUserConnectionSql(),
		                                                 socialProperties.getUserIdColumnName(),
		                                                 socialProperties.getProviderIdColumnName(),
		                                                 socialProperties.getProviderUserIdColumnName()),
		                                   connectionDataMapper, userId, connectionKey.getProviderId(),
		                                   connectionKey.getProviderUserId());
	}

	@Cacheable(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME, key = "#userId + '__' + #providerId",
			unless = "#result.size() == null || #result.size() == 0")
	public List<ConnectionDataDTO> findConnections(String userId, String providerId) {
		return jdbcTemplate.query(String.format("%s where %s = ? and %s = ? order by %s",
		                                        socialProperties.getSelectFromUserConnectionSql(),
		                                        socialProperties.getUserIdColumnName(),
		                                        socialProperties.getProviderIdColumnName(),
		                                        socialProperties.getRankColumnName()),
		                          connectionDataMapper, userId, providerId);
	}

	@Cacheable(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME, key = "#userId + '__' + #root.methodName",
			unless = "#result.size() == null || #result.size() == 0")
	public List<ConnectionDataDTO> findAllConnections(String userId) {
		return jdbcTemplate.query(String.format("%s where %s = ? order by %s, %s",
		                                        socialProperties.getSelectFromUserConnectionSql(),
		                                        socialProperties.getUserIdColumnName(),
		                                        socialProperties.getProviderIdColumnName(),
		                                        socialProperties.getRankColumnName()),
		                          connectionDataMapper, userId);
	}

	@Cacheable(cacheNames = USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME, key = "#userId + '__' + #parameters",
			unless = "#result.size() == null || #result.size() == 0")
	public List<ConnectionDataDTO>
	findConnectionsToUsers(MapSqlParameterSource parameters, String providerUsersCriteriaSql, String userId) {

		return new NamedParameterJdbcTemplate(jdbcTemplate)
				.query(String.format("%s where %s = :userId and %s order by %s, %s",
				                     socialProperties.getSelectFromUserConnectionSql(),
				                     socialProperties.getUserIdColumnName(),
				                     providerUsersCriteriaSql,
				                     socialProperties.getProviderIdColumnName(),
				                     socialProperties.getRankColumnName()),
				       parameters, connectionDataMapper);

	}

	private final ServiceProviderConnectionDataMapper connectionDataMapper = new ServiceProviderConnectionDataMapper();

	private final class ServiceProviderConnectionDataMapper implements RowMapper<ConnectionDataDTO> {

		@Override
		public ConnectionDataDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			return mapConnectionData(rs);
		}

		private ConnectionDataDTO mapConnectionData(ResultSet rs) throws SQLException {
			ConnectionDataDTO connectionDataDTO = new ConnectionDataDTO();
			connectionDataDTO.setProviderId(rs.getString(socialProperties.getProviderIdColumnName()));
			connectionDataDTO.setProviderUserId(rs.getString(socialProperties.getProviderUserIdColumnName()));
			connectionDataDTO.setDisplayName(rs.getString(socialProperties.getDisplayNameColumnName()));
			connectionDataDTO.setProfileUrl(rs.getString(socialProperties.getProfileUrlColumnName()));
			connectionDataDTO.setImageUrl(rs.getString(socialProperties.getImageUrlColumnName()));
			connectionDataDTO.setAccessToken(decrypt(rs.getString(socialProperties.getAccessTokenColumnName())));
			connectionDataDTO.setSecret(decrypt(rs.getString(socialProperties.getSecretColumnName())));
			connectionDataDTO.setRefreshToken(decrypt(rs.getString(socialProperties.getRefreshTokenColumnName())));
			connectionDataDTO.setExpireTime(expireTime(rs.getLong(socialProperties.getExpireTimeColumnName())));
			return connectionDataDTO;
		}

		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : null;
		}

		private Long expireTime(long expireTime) {
			return expireTime == 0 ? null : expireTime;
		}

	}

}
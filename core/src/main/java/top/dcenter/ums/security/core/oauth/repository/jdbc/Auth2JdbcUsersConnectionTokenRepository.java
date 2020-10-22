/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.oauth.repository.jdbc;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.dcenter.ums.security.core.oauth.config.RedisCacheAutoConfiguration;
import top.dcenter.ums.security.core.oauth.enums.EnableRefresh;
import top.dcenter.ums.security.core.oauth.repository.UsersConnectionTokenRepository;
import top.dcenter.ums.security.core.oauth.entity.AuthTokenPo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static top.dcenter.ums.security.core.oauth.enums.EnableRefresh.NO;
import static top.dcenter.ums.security.core.oauth.enums.EnableRefresh.YES;

/**
 * AuthToken 持久化服务
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/10 15:32
 */
@CacheConfig(cacheManager = "auth2RedisHashCacheManager")
public class Auth2JdbcUsersConnectionTokenRepository implements UsersConnectionTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    private final TextEncryptor textEncryptor;


    public Auth2JdbcUsersConnectionTokenRepository(JdbcTemplate auth2UserConnectionJdbcTemplate,
                                                   TextEncryptor textEncryptor) {
        this.jdbcTemplate = auth2UserConnectionJdbcTemplate;
        this.textEncryptor = textEncryptor;
    }

    @Cacheable(cacheNames = RedisCacheAutoConfiguration.USER_CONNECTION_CACHE_NAME, key = "'h:' + #tokenId")
    @Override
    public AuthTokenPo findAuthTokenById(String tokenId) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT `id`, `enableRefresh`, `providerId`, `accessToken`, `expireIn`, " +
                                                   "`refreshToken`, `uid`, `openId`, `accessCode`, `unionId`, `scope`, " +
                                                   "`tokenType`, `idToken`, `macAlgorithm`, `macKey`, `code`, " +
                                                   "`oauthToken`, `oauthTokenSecret`, `userId`, `screenName`, " +
                                                   "`oauthCallbackConfirmed`, `expireTime` " +
                                                   "FROM auth_token " +
                                                   "WHERE id = ?;",
                                           authTokenPoMapper, tokenId);
    }

    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @CachePut(cacheNames = RedisCacheAutoConfiguration.USER_CONNECTION_CACHE_NAME, key = "'h:' + #result.id")
    @Override
    public AuthTokenPo saveAuthToken(AuthTokenPo authToken) throws DataAccessException {
        jdbcTemplate.update("INSERT INTO auth_token(`enableRefresh` ,`providerId`, `accessToken`, `expireIn`, " +
                                    "`refreshToken`, `uid`, `openId`, `accessCode`, `unionId`, `scope`, `tokenType`, " +
                                    "`idToken`, `macAlgorithm`, `macKey`, `code`, `oauthToken`, `oauthTokenSecret`, " +
                                    "`userId`, `screenName`, `oauthCallbackConfirmed`, `expireTime`) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                            authToken.getEnableRefresh().getCode(),
                            authToken.getProviderId(),
                            encrypt(authToken.getAccessToken()),
                            authToken.getExpireIn(),
                            encrypt(authToken.getRefreshToken()),
                            authToken.getUid(),
                            authToken.getOpenId(),
                            encrypt(authToken.getAccessCode()),
                            authToken.getUnionId(),
                            // Google附带属性
                            authToken.getScope(),
                            authToken.getTokenType(),
                            encrypt(authToken.getIdToken()),
                            // 小米附带属性
                            authToken.getMacAlgorithm(),
                            encrypt(authToken.getMacKey()),
                            // 企业微信附带属性
                            encrypt(authToken.getCode()),
                            // Twitter附带属性
                            encrypt(authToken.getOauthToken()),
                            encrypt(authToken.getOauthTokenSecret()),
                            authToken.getUserId(),
                            authToken.getScreenName(),
                            authToken.getOauthCallbackConfirmed(),
                            // 过期时间, 基于 1970-01-01T00:00:00Z, 无过期时间默认为 -1
                            authToken.getExpireTime());
        // 获取 id
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", null, Long.class);
        authToken.setId(id);
        return authToken;
    }

    @CachePut(cacheNames = RedisCacheAutoConfiguration.USER_CONNECTION_CACHE_NAME, key = "'h:' + #result.id")
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public AuthTokenPo updateAuthToken(@NonNull AuthTokenPo authToken) throws DataAccessException {
        if (authToken.getId() == null)
        {
            throw new RuntimeException("authToken id cannot be null");
        }
        jdbcTemplate.update("UPDATE `auth_token` SET " +
                                    "`enableRefresh` = ?, " +
                                    "`providerId` = ?, " +
                                    "`accessToken` = ?, " +
                                    "`expireIn` = ?, " +
                                    "`refreshToken` = ?, " +
                                    "`uid` = ?, " +
                                    "`openId` = ?, " +
                                    "`accessCode` = ?, " +
                                    "`unionId` = ?, " +
                                    "`scope` = ?, " +
                                    "`tokenType` = ?, " +
                                    "`idToken` = ?, " +
                                    "`macAlgorithm` = ?, " +
                                    "`macKey` = ?, " +
                                    "`code` = ?, " +
                                    "`oauthToken` = ?, " +
                                    "`oauthTokenSecret` = ?, " +
                                    "`userId` = ?, " +
                                    "`screenName` = ?, " +
                                    "`oauthCallbackConfirmed` = ?, " +
                                    "`expireTime` = ? " +
                                    "WHERE `id` = ?;",
                            authToken.getEnableRefresh().getCode(),
                            authToken.getProviderId(),
                            encrypt(authToken.getAccessToken()),
                            authToken.getExpireIn(),
                            encrypt(authToken.getRefreshToken()),
                            authToken.getUid(),
                            authToken.getOpenId(),
                            encrypt(authToken.getAccessCode()),
                            authToken.getUnionId(),
                            // Google附带属性
                            authToken.getScope(),
                            authToken.getTokenType(),
                            encrypt(authToken.getIdToken()),
                            // 小米附带属性
                            authToken.getMacAlgorithm(),
                            encrypt(authToken.getMacKey()),
                            // 企业微信附带属性
                            encrypt(authToken.getCode()),
                            // Twitter附带属性
                            encrypt(authToken.getOauthToken()),
                            encrypt(authToken.getOauthTokenSecret()),
                            authToken.getUserId(),
                            authToken.getScreenName(),
                            authToken.getOauthCallbackConfirmed(),
                            // 过期时间, 基于 1970-01-01T00:00:00Z, 无过期时间默认为 -1
                            authToken.getExpireTime(),
                            authToken.getId());
        return authToken;
    }

    @CacheEvict(cacheNames = RedisCacheAutoConfiguration.USER_CONNECTION_CACHE_NAME,
            key = "'h:' + #tokenId", beforeInvocation = true)
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void delAuthTokenById(@NonNull String tokenId) throws DataAccessException {
        jdbcTemplate.update("DELETE FROM auth_token WHERE id = ?;", tokenId);
    }

    @Override
    public Long getMaxTokenId() throws IncorrectResultSizeDataAccessException {
        return jdbcTemplate.queryForObject("SELECT MAX(`id`) FROM `auth_token`", Long.class);
    }

    @Override
    public List<AuthTokenPo> findAuthTokenByExpireTimeAndBetweenId(@NonNull Long expiredTime, @NonNull Long startId,
                                                                   @NonNull Long endId) throws DataAccessException {
        return jdbcTemplate.query("SELECT `id`, `enableRefresh`, `providerId`, `accessToken`, `expireIn`, " +
                                   "`refreshToken`, `uid`, `openId`, `accessCode`, `unionId`, `scope`, " +
                                   "`tokenType`, `idToken`, `macAlgorithm`, `macKey`, `code`, " +
                                   "`oauthToken`, `oauthTokenSecret`, `userId`, `screenName`, " +
                                   "`oauthCallbackConfirmed`, `expireTime` " +
                                   "FROM auth_token " +
                                   "WHERE id BETWEEN ? AND ? AND `expireTime` <= ? " +
                                          "AND enableRefresh = " + YES.getCode() + ";",
                           authTokenPoMapper, startId, endId, expiredTime);
    }

    @CacheEvict(cacheNames = RedisCacheAutoConfiguration.USER_CONNECTION_CACHE_NAME,
            key = "'h:' + #tokenId", beforeInvocation = true)
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void updateEnableRefreshByTokenId(@NonNull EnableRefresh enableRefresh, @NonNull Long tokenId) throws DataAccessException {
        jdbcTemplate.update("update `auth_token` set `enableRefresh` = ? where id = ?",
                            enableRefresh.getCode(), tokenId);
    }

    private String encrypt(String text) {
        return text != null ? textEncryptor.encrypt(text) : null;
    }

    private final AuthTokenPoMapper authTokenPoMapper = new AuthTokenPoMapper();

    private final class AuthTokenPoMapper implements RowMapper<AuthTokenPo> {

        @Override
        public AuthTokenPo mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            return mapAuthToken(rs);
        }

        private AuthTokenPo mapAuthToken(ResultSet rs) throws SQLException {
            AuthTokenPo token = new AuthTokenPo();
            token.setId(rs.getLong("id"));
            int enableRefresh = rs.getInt("enableRefresh");
            token.setEnableRefresh(enableRefresh == YES.getCode() ? YES : NO);
            token.setProviderId(rs.getString("providerId"));
            token.setAccessToken(decrypt(rs.getString("accessToken")));
            token.setExpireIn(rs.getInt("expireIn"));
            token.setRefreshToken(decrypt(rs.getString("refreshToken")));
            token.setUid(rs.getString("uid"));
            token.setOpenId(rs.getString("openId"));
            token.setAccessCode(decrypt(rs.getString("accessCode")));
            token.setUnionId(rs.getString("unionId"));

            // Google附带属性
            token.setScope(rs.getString("scope"));
            token.setTokenType(rs.getString("tokenType"));
            token.setIdToken(decrypt(rs.getString("idToken")));

            // 小米附带属性
            token.setMacAlgorithm(rs.getString("macAlgorithm"));
            token.setMacKey(decrypt(rs.getString("macKey")));

            // 企业微信附带属性
            token.setCode(decrypt(rs.getString("code")));

            // Twitter附带属性
            token.setOauthToken(decrypt(rs.getString("oauthToken")));
            token.setOauthTokenSecret(decrypt(rs.getString("oauthTokenSecret")));
            token.setUserId(rs.getString("userId"));
            token.setScreenName(rs.getString("screenName"));
            token.setOauthCallbackConfirmed(rs.getBoolean("oauthCallbackConfirmed"));
            // 过期时间, 基于 1970-01-01T00:00:00Z, 无过期时间默认为 -1
            token.setExpireTime(rs.getLong("expireTime"));
            return token;
        }

        private String decrypt(String encryptedText) {
            return encryptedText != null ? textEncryptor.decrypt(encryptedText) : null;
        }

        @SuppressWarnings("unused")
        private Long expireTime(long expireTime) {
            return expireTime == 0 ? null : expireTime;
        }

    }
}
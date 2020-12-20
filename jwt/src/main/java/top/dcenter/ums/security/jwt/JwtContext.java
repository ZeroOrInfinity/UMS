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
package top.dcenter.ums.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.utils.UuidUtils;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.decoder.UmsNimbusJwtDecoder;
import top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy;
import top.dcenter.ums.security.jwt.exception.DuplicateRefreshTokenException;
import top.dcenter.ums.security.jwt.exception.JwtCreateException;
import top.dcenter.ums.security.jwt.exception.JwtExpiredException;
import top.dcenter.ums.security.jwt.exception.JwtInvalidException;
import top.dcenter.ums.security.jwt.exception.MismatchRefreshJwtPolicyException;
import top.dcenter.ums.security.jwt.exception.RefreshTokenInvalidException;
import top.dcenter.ums.security.jwt.properties.BearerTokenProperties;
import top.dcenter.ums.security.jwt.properties.JwtCacheProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.redis.connection.RedisStringCommands.SetOption.SET_IF_ABSENT;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.REFRESH_TOKEN_DUPLICATE;
import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcTraceId;
import static top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames.USER_ID;
import static top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy.AUTO_RENEW;
import static top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy.REFRESH_TOKEN;

/**
 * JWT 上下文
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.8 15:40
 */
@SuppressWarnings("FieldMayBeFinal")
@Slf4j
public final class JwtContext {

    public static final String KEY_ALGORITHM = "RSA";
    public static final int KEY_SIZE = 2048;
    public static final String BEARER = "Bearer ";
    /**
     * 临时存储 jwt refresh token 的值
     */
    public static final String TEMPORARY_JWT_REFRESH_TOKEN = "TEMPORARY_JWT_REFRESH_TOKEN";

    /**
     * JSON Web Signature (JWS) signer.<br>
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static JWSSigner signer = null;
    /**
     * JSON Web Signature (JWS) algorithm.<br>
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static String jwsAlgorithm = null;
    /**
     * JSON Web Signature (JWS) kid.<br>
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static String kid = null;
    /**
     * JWT 的有效期, 默认: 1 小时
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static Duration timeout = Duration.ofHours(1);
    /**
     * JWT 不同服务器间的时钟偏差, 时钟可能存在偏差, 设置时钟偏移量以消除不同服务器间的时钟偏差的影响, 默认: 0 秒.
     * 注意: 此默认值适合 "单服务器" 应用, "多服务器" 应用请更改此值.
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static Duration clockSkew = Duration.ofSeconds(0);

    /**
     * JWT  刷新处理策略, 默认: {@link JwtRefreshHandlerPolicy#REJECT}
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static JwtRefreshHandlerPolicy refreshHandlerPolicy = JwtRefreshHandlerPolicy.REJECT;
    /**
     * JWT 的 BearerToken 属性
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static BearerTokenProperties bearerToken = null;
    /**
     * RedisConnectionFactory
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static RedisConnectionFactory redisConnectionFactory = null;
    /**
     * RedisConnectionFactory
     * 如果支持 JWT 功能, 通过 {@code top.dcenter.ums.security.jwt.config.JwtAutoConfiguration#afterPropertiesSet()}
     * 方法注入.
     */
    private volatile static JwtCacheProperties cacheProperties = null;

    /**
     * 生成 {@link JWK}, 与下面的方式等效:
     * <pre>
     * // Generate 2048-bit RSA key pair in JWK format, attach some metadata
     * RSAKey jwk = new RSAKeyGenerator(2048)
     *         .keyUse(KeyUse.SIGNATURE) // indicate the intended use of the key
     *         .keyID(kid) // give the key a unique ID
     *         .generate();
     * </pre>
     *
     * @param kid       Sets the ID (kid) of the JWK.  The key ID can be used to match a specific key.
     *                  This can be used, for instance, to choose a key within a JWKSet during key rollover.
     *                  The key ID may also correspond to a JWS/JWE kid header parameter value.
     * @param keyUse    Enumeration of public key uses. Represents the use parameter in a JSON Web Key (JWK).
     *                  Public JWK use values: {@link KeyUse#SIGNATURE} {@link KeyUse#ENCRYPTION}
     * @return  返回 {@link JWK}
     * @throws NoSuchAlgorithmException 异常
     */
    @SuppressWarnings("unused")
    public static JWK generateJwk(String kid, KeyUse keyUse) throws NoSuchAlgorithmException {

        // Generate 2048-bit RSA key pair
        KeyPairGenerator gen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        gen.initialize(KEY_SIZE);
        KeyPair keyPair = gen.generateKeyPair();

        // Convert to JWK format
        return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                .privateKey((RSAPrivateKey)keyPair.getPrivate())
                .keyUse(keyUse)
                .keyID(kid)
                .build();
    }

    /**
     * 生成 {@link JWK}, 与下面的方式等效:
     * <pre>
     * // Generate 2048-bit RSA key pair in JWK format, attach some metadata
     * RSAKey jwk = new RSAKeyGenerator(2048)
     *         .keyUse(KeyUse.SIGNATURE) // indicate the intended use of the key
     *         .keyID(kid) // give the key a unique ID
     *         .generate();
     * </pre>
     * @param keyPair   此类是密钥对（公共密钥和私有密钥）的简单持有者。 它不强制执行任何安全性，并且在初始化时应将其视为私钥.
     * @param kid       Sets the ID (kid) of the JWK.  The key ID can be used to match a specific key.
     *                  This can be used, for instance, to choose a key within a JWKSet during key rollover.
     *                  The key ID may also correspond to a JWS/JWE kid header parameter value.
     * @param keyUse    Enumeration of public key uses. Represents the use parameter in a JSON Web Key (JWK).
     *                  Public JWK use values: {@link KeyUse#SIGNATURE} {@link KeyUse#ENCRYPTION}
     * @return  返回 {@link JWK}
     */
    public static JWK generateJwk(KeyPair keyPair, String kid, KeyUse keyUse) {

        // Convert to JWK format
        return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                .privateKey((RSAPrivateKey)keyPair.getPrivate())
                .keyUse(keyUse)
                .keyID(kid)
                .build();
    }

    /**
     * 如果符合条件, 生成 {@link Jwt} 并转换为 {@link JwtAuthenticationToken}, 不符合条件则原样返回.
     * @param authentication            authentication
     * @param generateClaimsSetService  generateClaimsSetService
     * @return  如果符合条件, 返回 {@link JwtAuthenticationToken}, 不符合条件则原样返回 {@link Authentication}
     * @throws JwtCreateException 转换异常
     */
    @NonNull
    public static Authentication createJwtAndToJwtAuthenticationToken(@NonNull Authentication authentication,
                                     @Nullable GenerateClaimsSetService generateClaimsSetService) throws JwtCreateException {
        // 生成 Jwt 并转换为 JwtAuthenticationToken
        if (nonNull(generateClaimsSetService) && isSupportJwt(authentication)) {
            try {
                Jwt jwt = createJwt(generateClaimsSetService.generateClaimsSet(authentication));

                setBearerTokenAndRefreshTokenToHeader(jwt, TRUE);
                return toJwtAuthenticationToken(jwt, authentication);
            }
            catch (Exception e) {
                String msg = String.format("创建 jwt token 失败: %s", authentication);
                log.error(msg, e);
                throw new JwtCreateException(ErrorCodeEnum.CREATE_JWT_ERROR, getMdcTraceId());
            }
        }
        // 原样返回
        return authentication;
    }

    /**
     * 重置旧 {@link Jwt} 的 exp/iat/nbf 的时间戳并返回重新签名后的 {@link Jwt},
     * 注意: {@link Jwt} 中 tokenValue 的"日期"都以"时间戳"表示且"时间戳"以秒为单位
     * @param jwt           过期的 {@link Jwt}
     * @param jwtDecoder    {@link UmsNimbusJwtDecoder}
     * @return  返回 重置旧 {@link Jwt} 的 exp/iat/nbf 的时间戳并重新签名后的 {@link Jwt}
     * @throws ParseException       重置 jwt 异常
     * @throws JOSEException        重置 jwt 异常
     * @throws JwtInvalidException  jwt 失效异常
     */
    @NonNull
    public static Jwt resetJwtExpOfAutoRenewPolicy(@NonNull Jwt jwt, @NonNull UmsNimbusJwtDecoder jwtDecoder,
                                                   @NonNull JwtRefreshHandlerPolicy policy) throws ParseException, JOSEException, JwtInvalidException {

        if (!AUTO_RENEW.equals(policy)) {
            throw new MismatchRefreshJwtPolicyException(ErrorCodeEnum.REFRESH_JWT_POLICY_MISMATCH, getMdcTraceId());
        }

        // 1. 校验 jwt 是否在黑名单, 是否需要重新认证(reAuth)
        BlacklistType blacklistType = jtiInTheBlacklist(jwt.getId());
        if (blacklistType.isInBlacklist()) {
            String blackListValue = blacklistType.getOneTimeNewJwtValue();
            if (nonNull(blackListValue)) {
                Jwt newJwt = jwtDecoder.decodeNotValidate(blackListValue);
                // 新的 jwt 设置 header
                setBearerTokenAndRefreshTokenToHeader(newJwt, FALSE);
                return newJwt;
            }
            else {
                // 需要重新认证
                throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
            }
        }

        // 2. 重置jwt
        Map<String, Object> claims = jwt.getClaims();
        // 重置 exp/iat/nbf 的时间戳
        Instant now = Instant.now();
        claims.put(JwtClaimNames.EXP, now.plusSeconds(timeout.getSeconds()));
        Instant iat = (Instant) claims.get(JwtClaimNames.IAT);
        Instant nbf = (Instant) claims.get(JwtClaimNames.NBF);
        long nowEpochSecond = now.getEpochSecond();
        if (nonNull(iat)) {
            claims.put(JwtClaimNames.IAT, nowEpochSecond);
            if (nonNull(nbf)) {
                claims.put(JwtClaimNames.NBF, nowEpochSecond + (nbf.getEpochSecond() - iat.getEpochSecond()));
            }
        }
        // 转换其他 Claims 的 Instant 类型为以秒记的时间戳
        claims.entrySet()
              .stream()
              .filter((entry -> entry.getValue() instanceof Instant))
              .forEach(entry -> entry.setValue(((Instant) entry.getValue()).getEpochSecond()));

        Jwt newJwt = createJwt(getJwsHeader(), toJwtClaimsSet(claims));

        // 3. 添加黑名单
        addBlacklist(jwt, newJwt);

        // 4. 新的 jwt 设置 header, 并检查 refresh token 是否需要重新生成.
        setBearerTokenAndRefreshTokenToHeader(newJwt, FALSE);

        return newJwt;
    }


    /**
     * jti 是否在黑名单中; <br>
     * 此方法已调用 {@link BlacklistType#getBlacklistType(String)}, 如果返回值为
     * {@link BlacklistType#IN_BLACKLIST_AND_HAS_NEW_JWT}, 可调用 {@link BlacklistType#getOneTimeNewJwtValue()} 获取新的
     * jwt 字符串.
     * @param jti   {@link JwtClaimNames#JTI} 的值
     * @return  返回 {@link BlacklistType}
     */
    @NonNull
    public static BlacklistType jtiInTheBlacklist(@NonNull String jti) {
        byte[] result = getConnection().get(getBlacklistKey(jti));

        String blacklistValue;
        if (isNull(result)) {
            blacklistValue = null;
        }
        else {
            blacklistValue = new String(result, StandardCharsets.UTF_8);
        }

        return BlacklistType.getBlacklistType(blacklistValue);
    }

    /**
     * 根据 refresh token 获取新 Jwt 处理器
     *
     * @param refreshToken             refresh token
     * @param request                  {@link HttpServletRequest}
     * @param jwtDecoder               {@link JwtDecoder}
     * @param umsUserDetailsService    {@link UmsUserDetailsService}
     * @param generateClaimsSetService {@link GenerateClaimsSetService}
     * @return 返回新的 {@link Jwt}
     * @throws JwtCreateException           Jwt 创建 异常
     * @throws RefreshTokenInvalidException refreshToken 失效异常
     */
    @NonNull
    public static Jwt generateJwtByRefreshToken(@NonNull String refreshToken,
                                                @NonNull HttpServletRequest request,
                                                @NonNull JwtDecoder jwtDecoder,
                                                @NonNull UmsUserDetailsService umsUserDetailsService,
                                                @NonNull GenerateClaimsSetService generateClaimsSetService)
            throws JwtCreateException, RefreshTokenInvalidException {

        // 1. 判断 refreshToken 是否有效
        String userIdByRefreshToken = getUserIdByRefreshToken(refreshToken);
        if (isNull(userIdByRefreshToken)) {
            throw new RefreshTokenInvalidException(ErrorCodeEnum.JWT_REFRESH_TOKEN_INVALID, getMdcTraceId());
        }

        // 2. 获取用户信息, 并创建 Authentication
        UserDetails userDetails = umsUserDetailsService.loadUserByUserId(userIdByRefreshToken);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 3. 创建 JWT
        JWTClaimsSet jwtClaimsSet = generateClaimsSetService.generateClaimsSet(authenticationToken);
        Jwt jwt;
        try {
            jwt = createJwt(jwtClaimsSet);
        }
        catch (JOSEException | ParseException e) {
            log.error(e.getMessage(), e);
            throw new JwtCreateException(ErrorCodeEnum.CREATE_JWT_ERROR, getMdcTraceId());
        }

        // 4. 获取旧的 jwt, 未失效则添加黑名单, 以解决刷新 jwt 而引发的并发访问问题.
        setOldJwtToBlacklist(refreshToken, request, jwtDecoder, userIdByRefreshToken, jwt);

        // 5. 设置 jwt 到 header
        setBearerTokenAndRefreshTokenToHeader(jwt, false);

        return jwt;
    }

    /**
     * 从 request 中获取 refreshToken 或 bearerToken
     *
     * @param request       {@link HttpServletRequest}
     * @param parameterName request 中的参数名称
     * @param headerName    request 中的 header 的名称
     * @return 返回 refreshToken 或 bearerToken, 如果 request 不存在对应的值则返回 null.
     */
    @Nullable
    public static String getRefreshTokenOrBearerToken(@NonNull HttpServletRequest request,
                                                      @NonNull String parameterName,
                                                      @NonNull String headerName) {

        String token;
        Boolean allowFormEncodedBodyParameter = bearerToken.getAllowFormEncodedBodyParameter();
        Boolean allowUriQueryParameter = bearerToken.getAllowUriQueryParameter();
        if (allowFormEncodedBodyParameter || allowUriQueryParameter) {
            // 从 request 中获取 refreshToken 或 bearerToken
            token = request.getParameter(parameterName);
        }
        else {
            // 从 header 中获取 refreshToken 或 bearerToken
            token = request.getHeader(headerName);
        }
        return token;
    }

    /**
     * 获取 JWT 不同服务器间的时钟偏差, 通过 {@code ums.jwt.clockSkew } 属性设置.
     * @return  返回时钟偏差的 {@link Duration}
     */
    @NonNull
    public static Duration getClockSkew() {
        return clockSkew;
    }

    /**
     * 从 session 中获取 name 为 {@link #TEMPORARY_JWT_REFRESH_TOKEN} 的 值
     * @return  返回 jwt refresh token, 如果不存在则返回 null
     */
    @Nullable
    public static String getJwtRefreshToken() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return (String) requestAttributes.getAttribute(TEMPORARY_JWT_REFRESH_TOKEN, SCOPE_SESSION);
    }

    /**
     * 是否通过 refreshToken 刷新 jwt
     * @return 返回 true 表示通过 refreshToken 刷新 jwt
     */
    @NonNull
    public static Boolean isRefreshJwtByRefreshToken() {
        return REFRESH_TOKEN.equals(refreshHandlerPolicy);
    }

    /**
     * 获取 jwt 字符串, 如果 authentication 不是 {@link AbstractOAuth2TokenAuthenticationToken} 或
     * {@link BearerTokenProperties#getAllowFormEncodedBodyParameter()} = false 则返回 null
     * @param authentication    authentication
     * @return 返回 jwt 字符串, 如果 authentication 不是 {@link AbstractOAuth2TokenAuthenticationToken} 或
     *         {@link BearerTokenProperties#getAllowFormEncodedBodyParameter()} = false 则返回 null
     */
    @Nullable
    public static String getJwtStringIfAllowBodyParameter(@NonNull Authentication authentication) {
        Boolean allowFormEncodedBodyParameter = bearerToken.getAllowFormEncodedBodyParameter();
        if ((authentication instanceof AbstractOAuth2TokenAuthenticationToken) && allowFormEncodedBodyParameter)
        {
            //noinspection unchecked
            AbstractOAuth2TokenAuthenticationToken<Jwt> jwtAuthenticationToken =
                    ((AbstractOAuth2TokenAuthenticationToken<Jwt>) authentication);
            return jwtAuthenticationToken.getToken().getTokenValue();
        }

        return null;
    }

    /**
     * 添加黑名单
     * @param oldJwt    要加入黑名单的 {@link Jwt}
     */
    public static void addBlacklist(@NonNull Jwt oldJwt) {
        addBlacklist(oldJwt, BlacklistType.IN_BLACKLIST.name().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 添加黑名单
     * @param oldJwt    要加入黑名单的 {@link Jwt}
     * @param newJwt    替换 oldJwt 的 {@link Jwt}, 用于旧 jwt 失效引发的并发访问问题.
     */
    private static void addBlacklist(@NonNull Jwt oldJwt, @NonNull Jwt newJwt) {
        addBlacklist(oldJwt, newJwt.getTokenValue().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取旧的 jwt, 未失效则添加黑名单, 以解决刷新 jwt 而引发的并发访问问题.
     * @param refreshToken          refreshToken
     * @param request               {@link HttpServletRequest}
     * @param jwtDecoder            {@link JwtDecoder}
     * @param userIdByRefreshToken  userIdByRefreshToken
     * @param jwt                   {@link Jwt}
     */
    private static void setOldJwtToBlacklist(@NonNull String refreshToken, @NonNull HttpServletRequest request,
                                             @NonNull JwtDecoder jwtDecoder,
                                             @NonNull String userIdByRefreshToken,
                                             @NonNull Jwt jwt) {

        Jwt oldJwt = getJwtByRequest(request, jwtDecoder);

        if (nonNull(oldJwt)) {
            // 校验新 jwt 与 旧 jwt 是否相同 userId
            Object oldUserId = oldJwt.getClaim(USER_ID.getClaimName());
            if (!Objects.equals(oldUserId, userIdByRefreshToken)) {
                log.error("oldUserId: {} 与 userIdByRefreshToken: {} 不匹配, refreshToken: {}",
                          oldUserId, userIdByRefreshToken, refreshToken);
                // userId 与 refreshToken 不匹配, 删除 refreshToken
                getConnection().del(getRefreshTokenKey(refreshToken));
                throw new RefreshTokenInvalidException(ErrorCodeEnum.JWT_REFRESH_TOKEN_INVALID, getMdcTraceId());
            }
            addBlacklist(oldJwt, jwt);
        }
    }

    /**
     * 从 request 中获取 jwt
     * @param request               {@link HttpServletRequest}
     * @param jwtDecoder            {@link JwtDecoder}
     * @return  返回 {@link Jwt}, 如果 request 中无 jwt 字符串或 jwt 字符串已失效, 则返回 null
     */
    @Nullable
    private static Jwt getJwtByRequest(@NonNull HttpServletRequest request, @NonNull JwtDecoder jwtDecoder) {
        Jwt oldJwt;

        // 获取 jwt 字符串
        String jwtString = getRefreshTokenOrBearerToken(request,
                                                        bearerToken.getBearerTokenParameterName(),
                                                        bearerToken.getBearerTokenHeaderName());

        if (isNull(jwtString)) {
            return null;
        }

        // 转换为 jwt
        try {
            if (jwtDecoder instanceof UmsNimbusJwtDecoder) {
                oldJwt = ((UmsNimbusJwtDecoder) jwtDecoder).decodeNotRefreshToken(jwtString);
            }
            else {
                oldJwt = jwtDecoder.decode(jwtString);
            }
        }
        catch (JwtException | JwtInvalidException | JwtExpiredException e) {
            oldJwt = null;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            oldJwt = null;
        }

        // 返回 jwt
        return oldJwt;
    }

    /**
     * 根据 refreshToken 获取 UserId
     * @param refreshToken  refreshToken
     * @return  如果缓存中存在 refreshToken, 表示 refreshToken 有效, 返回 userId, 如果返回 null, 表示 refreshToken 无效.
     */
    @Nullable
    private static String getUserIdByRefreshToken(@NonNull String refreshToken) {
        byte[] result = getConnection().get(getRefreshTokenKey(refreshToken));

        if (isNull(result)) {
            return null;
        }

        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * 生成 refresh token 且与 userId 一起保存到 redis 中
     * @param userId 用户id
     * @return  返回 refresh token
     */
    @NonNull
    private static String generateAndSaveRefreshToken(@NonNull String userId) {
        String uuid = UuidUtils.getUUID();
        for (int i = 0; !saveRefreshToken(userId, uuid); i++) {
            uuid = UuidUtils.getUUID();
            // 最多重复三次
            if (i == 2) {
                throw new DuplicateRefreshTokenException(REFRESH_TOKEN_DUPLICATE, getMdcTraceId());
            }
        }
        return uuid;
    }

    /**
     * 添加黑名单
     * @param oldJwt    要加入黑名单的 {@link Jwt}
     */
    private static void addBlacklist(@NonNull Jwt oldJwt, byte[] value) {
        Instant expiresAt = oldJwt.getExpiresAt();
        if (isNull(expiresAt)) {
            return;
        }

        Instant now = Instant.now();
        // 旧的 jwt 还在有效期内
        if (expiresAt.isAfter(now.minus(clockSkew))) {
            getConnection().set(getBlacklistKey(oldJwt.getId()),
                                value,
                                Expiration.seconds(expiresAt.getEpochSecond() - now.getEpochSecond() + clockSkew.getSeconds()),
                                SET_IF_ABSENT);
        }
    }

    /**
     * 保存 refresh token , 如果返回 false, 视为 refresh token 重复, 需重新生成 refresh token
     * @param userId        用户 id
     * @param refreshToken  refresh token
     * @return 返回 true, 表示保存 refresh token 成功, 如果返回 false, 视为 refresh token 重复, 需重新生成 refresh token .
     */
    @NonNull
    private static Boolean saveRefreshToken(@NonNull String userId, @NonNull String refreshToken) {
        Boolean isSuccess = getConnection().set(getRefreshTokenKey(refreshToken),
                                                userId.getBytes(StandardCharsets.UTF_8),
                                                Expiration.from(cacheProperties.getRefreshTokenTtl()),
                                                SET_IF_ABSENT);

        if (isNull(isSuccess)) {
            return false;
        }

        return isSuccess;
    }

    /**
     * 获取 jwt refresh token key
     * @param refreshToken  refresh token
     * @return  返回 refresh token key
     */
    @NonNull
    private static byte[] getRefreshTokenKey(String refreshToken) {
        return cacheProperties.getRefreshTokenPrefix().concat(refreshToken).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 获取 jwt 黑名单 key
     * @param jti   {@link JwtClaimNames#JTI}
     * @return  返回 jwt 黑名单 key.
     */
    @NonNull
    private static byte[] getBlacklistKey(String jti) {
        return cacheProperties.getBlacklistPrefix().concat(jti).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 获取 {@link RedisConnection}
     * @return  返回 {@link RedisConnection}
     */
    private static RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }

    /**
     * 设置 bearerToken 与 refreshToken 到 Header<br>
     * 注意: <br>
     * 1. {@link BearerTokenProperties#getAllowFormEncodedBodyParameter()} = false 才会设置 bearerToken 与 refreshToken 到 Header.<br>
     * 2. refreshToken 只有在 {@link JwtRefreshHandlerPolicy#REFRESH_TOKEN} 时才回设置.
     * @param jwt                       {@link Jwt}
     * @param isGenerateRefreshToken    是否需要重新生成 refreshToken
     */
    private static void setBearerTokenAndRefreshTokenToHeader(@NonNull Jwt jwt, @NonNull Boolean isGenerateRefreshToken) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        // 获取 bearerToken
        String bearerToken = BEARER + jwt.getTokenValue();
        if (isNull(response) || isNull(JwtContext.bearerToken)) {
            throw new IllegalStateException("HttpServletResponse is closed or does not support setting bearer token to header");
        }

        if (isGenerateRefreshToken) {
            // 设置 refreshToken 到 header
            if (REFRESH_TOKEN.equals(JwtContext.refreshHandlerPolicy)) {
                // 生成 refreshToken 并缓存进 redis
                String refreshToken = generateAndSaveRefreshToken(jwt.getClaimAsString(USER_ID.getClaimName()));
                String refreshTokenHeaderName = JwtContext.bearerToken.getRefreshTokenHeaderName();
                if (!JwtContext.bearerToken.getAllowFormEncodedBodyParameter()) {
                    // 设置到请求头
                    response.setHeader(refreshTokenHeaderName, refreshToken);
                }
                else {
                    // 临时设置到 session, 再通过认证成功处理器获取 refresh token 通过 json 返回
                    requestAttributes.setAttribute(TEMPORARY_JWT_REFRESH_TOKEN, refreshToken, SCOPE_SESSION);
                }
            }
        }

        // 当 jwt 刷新策略为 JwtRefreshHandlerPolicy#AUTO_RENEW 时, 刷新的 jwt 直接设置到 header 中, 前端可以从相应的 header 中获取.
        boolean isAutoRenewPolicy = AUTO_RENEW.equals(JwtContext.refreshHandlerPolicy);

        if (!JwtContext.bearerToken.getAllowFormEncodedBodyParameter() || isAutoRenewPolicy) {
            String bearerTokenHeaderName = JwtContext.bearerToken.getBearerTokenHeaderName();
            response.setHeader(bearerTokenHeaderName, bearerToken);
        }
    }

    /**
     * 是否支持 jwt
     * @param authentication    {@link Authentication}
     * @return  支持 jwt 则返回 true, 否则返回 false
     */
    @NonNull
    private static Boolean isSupportJwt(@NonNull Authentication authentication) {
        // 是否支持 jwt
        return nonNull(signer) && !(authentication instanceof AbstractOAuth2TokenAuthenticationToken);
    }

    /**
     * 转换为 {@link JwtAuthenticationToken}
     * @param jwt    {@link Jwt}
     * @param authentication    {@link Authentication}
     * @return  则返回 {@link JwtAuthenticationToken}
     */
    @NonNull
    private static JwtAuthenticationToken toJwtAuthenticationToken(@NonNull Jwt jwt, @NonNull Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, authentication.getAuthorities(), authentication.getName());
        jwtAuthenticationToken.setDetails(authentication.getDetails());
        return jwtAuthenticationToken;
    }

    /**
     * map 转换为 {@link JWTClaimsSet}
     * @param claims    claims map
     * @return  返回 {@link JWTClaimsSet}
     */
    @NonNull
    private static JWTClaimsSet toJwtClaimsSet(@NonNull Map<String, Object> claims) {
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        claims.forEach(builder::claim);
        return builder.build();
    }

    /**
     * 生成 {@link Jwt}
     * @param claimsSet         JWT Payload(负载), 注意: {@link JWTClaimsSet} 中"日期"都以"时间戳"表示且"时间戳"以秒为单位
     * @return                  返回 {@link Jwt}
     * @throws JOSEException    转换异常
     */
    @NonNull
    private static Jwt createJwt(@NonNull JWTClaimsSet claimsSet) throws JOSEException, ParseException {

        requireNonNull(signer, "signer 不存在, 不支持 JWT 功能");

        JWSHeader jwsHeader = getJwsHeader();

        return createJwt(jwsHeader, claimsSet);
    }

    /**
     * 生成 {@link Jwt}
     * @param jwsHeader         {@link JWSHeader}
     * @param claimsSet         JWT Payload(负载), 注意: {@link JWTClaimsSet} 中"日期"都以"时间戳"表示且"时间戳"以秒为单位
     * @return                  返回 {@link Jwt}
     * @throws JOSEException    转换异常
     */
    @NonNull
    private static Jwt createJwt(@NonNull JWSHeader jwsHeader, @NonNull JWTClaimsSet claimsSet) throws JOSEException,
            ParseException {

        requireNonNull(signer, "signer 不存在, 不支持 JWT 功能");

        SignedJWT signedjwt = new SignedJWT(jwsHeader, claimsSet);

        // Compute the RSA signature
        signedjwt.sign(signer);

        // To serialize to compact form, produces something like
        // eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
        // mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
        // maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
        // -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
        String tokenValue =  signedjwt.serialize();

        Long issueTime = claimsSet.getLongClaim(JwtClaimNames.IAT);
        Instant issueAt = null;
        if (nonNull(issueTime)) {
            issueAt = Instant.ofEpochSecond(issueTime);
        }

        return new Jwt(tokenValue, issueAt,
                       Instant.ofEpochSecond(claimsSet.getLongClaim(JwtClaimNames.EXP)),
                       jwsHeader.toJSONObject(), claimsSet.getClaims());
    }

    /**
     * 生成 JwsHeader
     * @return  返回 {@link JWSHeader}
     */
    @NonNull
    private static JWSHeader getJwsHeader() {
        requireNonNull(jwsAlgorithm, "未设置 jwsAlgorithm, 不支持 JWT 功能");

        JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.parse(jwsAlgorithm));
        builder.type(JOSEObjectType.JWT);
        if (hasText(kid)) {
            builder.keyID(kid);
        }
        return builder.build();
    }

    /**
     * 黑名单类型
     * @author YongWu zheng
     * @version V2.0  Created by 2020.12.17 23:25
     */
    enum BlacklistType {
        /**
         * 不在黑名单
         */
        NOT_IN_BLACKLIST {
            @Override
            @Nullable
            public String getOneTimeNewJwtValue() {
                return null;
            }

            @Override
            @NonNull
            public Boolean isInBlacklist() {
                return false;
            }
        },
        /**
         * 在黑名单中
         */
        IN_BLACKLIST {
            @Override
            @Nullable
            public String getOneTimeNewJwtValue() {
                return null;
            }

            @Override
            @NonNull
            public Boolean isInBlacklist() {
                return true;
            }
        },
        /**
         * 在黑名单中, 但缓存中存储有新的且有效 JWT
         */
        IN_BLACKLIST_AND_HAS_NEW_JWT {
            @Override
            @NonNull
            public Boolean isInBlacklist() {
                return true;
            }

            @Override
            @Nullable
            public String getOneTimeNewJwtValue() {
                try {
                    return JWT_VALUE.get();
                }
                finally {
                    JWT_VALUE.remove();
                }
            }
        };


        private static final ThreadLocal<String> JWT_VALUE = new ThreadLocal<>();

        /**
         * 通过 blacklistValue 来获取 {@link BlacklistType}
         * @param blacklistValue    存储在 jwt 缓存黑名单中的值
         * @return  返回 {@link BlacklistType}
         */
        @NonNull
        public static BlacklistType getBlacklistType(@Nullable String blacklistValue) {
            if (isNull(blacklistValue)) {
                return NOT_IN_BLACKLIST;
            }
            if (IN_BLACKLIST.name().equals(blacklistValue))
            {
                return IN_BLACKLIST;
            }
            JWT_VALUE.set(blacklistValue);
            return IN_BLACKLIST_AND_HAS_NEW_JWT;
        }

        /**
         * 获取一次性的新的 jwt 字符串.<br>
         * 调用一次 {@link #getBlacklistType(String)} 方法后, 只能在第一次调用此方法时才能获取到有效返回值, 第二次(n次)调用返回 null 值. <br>
         * 示例:<br>
         * 1. BlacklistType 为 IN_BLACKLIST_AND_HAS_NEW_JWT 的情况
         * <pre>
         *     // type = IN_BLACKLIST_AND_HAS_NEW_JWT
         *     BlacklistType type = getBlacklistType("xxxxx.xxxxxx.xxxxxx");
         *     // token = "xxxxx.xxxxxx.xxxxxx"
         *     String token = type.getOneTimeNewJwtValue();
         *     // token = null
         *     token = type.getOneTimeNewJwtValue();
         *
         *     // type = IN_BLACKLIST_AND_HAS_NEW_JWT
         *     type = getBlacklistType("xxxxx.xxxxxx.xxxxxx2");
         *     // token = "xxxxx.xxxxxx.xxxxxx2"
         *     token = type.getOneTimeNewJwtValue();
         *     // token = null
         *     token = type.getOneTimeNewJwtValue();
         * </pre>
         * 2. BlacklistType 为 IN_BLACKLIST 或 NOT_IN_BLACKLIST 的情况
         * <pre>
         *     // type = NOT_IN_BLACKLIST
         *     BlacklistType type = getBlacklistType(null);
         *     // newJwtToken = null
         *     String newJwtToken = type.getOneTimeNewJwtValue();
         *     // newJwtToken = null
         *     newJwtToken = type.getOneTimeNewJwtValue();
         *
         *     // type = IN_BLACKLIST
         *     type = getBlacklistType("IN_BLACKLIST");
         *     // newJwtToken = null
         *     newJwtToken = type.getOneTimeNewJwtValue();
         *     // newJwtToken = null
         *     newJwtToken = type.getOneTimeNewJwtValue();
         * </pre>
         * @return 返回新的 jwt token 或 null 值
         */
        @Nullable
        public abstract String getOneTimeNewJwtValue();

        /**
         * 是否在黑名单中
         * @return  返回 true 表示在黑名单中.
         */
        @NonNull
        public abstract Boolean isInBlacklist();

    }

}

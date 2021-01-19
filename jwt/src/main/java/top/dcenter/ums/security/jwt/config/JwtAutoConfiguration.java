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
package top.dcenter.ums.security.jwt.config;

import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.jwt.JwtContext;
import top.dcenter.ums.security.jwt.api.cache.service.JwtCacheTransformService;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkEndpointPermissionService;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkSetUriPassHeaders;
import top.dcenter.ums.security.jwt.api.id.service.JwtIdService;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.controller.JwtRefreshTokenController;
import top.dcenter.ums.security.jwt.decoder.UmsNimbusJwtDecoder;
import top.dcenter.ums.security.jwt.endpoint.JwkEndpoint;
import top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy;
import top.dcenter.ums.security.jwt.factory.KeyStoreKeyFactory;
import top.dcenter.ums.security.jwt.properties.BearerTokenProperties;
import top.dcenter.ums.security.jwt.properties.JwtBlacklistProperties;
import top.dcenter.ums.security.jwt.properties.JwtProperties;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;
import static top.dcenter.ums.security.common.utils.ReflectionUtil.setFieldValue;
import static top.dcenter.ums.security.jwt.properties.JwtProperties.MACS_SECRET_LENGTH;

/**
 * jwt 自动配置<br>
 * Jwt token 的 keyPair 签名 key:<br>
 * 1. 生成密钥键值对命令：keytool -genkeypair -alias zyw -keyalg RSA -keypass 123456 -keystore zyw.jks -storepass 123456 <br>
 * 2. 生成公钥命令：keytool -list -rfc --keystore zyw.jks | openssl x509 -inform pem -pubkey <br>
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.2 9:55
 */
@SuppressWarnings("jol")
@Configuration
@Order(99)
@AutoConfigureAfter({JwtPropertiesAutoConfiguration.class, JwtServiceAutoConfiguration.class})
@ConditionalOnProperty(prefix = "ums.jwt", name = "enable", havingValue = "true")
@Slf4j
public class JwtAutoConfiguration implements InitializingBean {

    /**
     * {@link JwtContext} 的 signer 字段名称
     */
    public static final String SIGNER_PARAM_NAME = "signer";
    /**
     * {@link JwtContext} 的 jwsAlgorithm 字段名称
     */
    public static final String JWS_ALGORITHM_PARAM_NAME = "jwsAlgorithm";
    /**
     * {@link JwtContext} 的 kid 字段名称
     */
    public static final String KID_PARAM_NAME = "kid";
    /**
     * {@link JwtContext} 的 timeout 字段名称
     */
    public static final String JWT_TIMEOUT = "timeout";
    /**
     * {@link JwtContext} 的 clockSkew 字段名称
     */
    public static final String CLOCK_SKEW = "clockSkew";
    /**
     * {@link JwtContext} 的 bearerToken 字段名称
     */
    public static final String BEARER_TOKEN = "bearerToken";
    /**
     * {@link JwtContext} 的 redisConnectionFactory 字段名称
     */
    public static final String REDIS_CONNECTION_FACTORY = "redisConnectionFactory";
    /**
     * {@link JwtContext} 的 blacklistProperties 字段名称
     */
    public static final String BLACKLIST_PROPERTIES = "blacklistProperties";
    /**
     * {@link JwtContext} 的 refreshHandlerPolicy 字段名称
     */
    public static final String REFRESH_HANDLER_POLICY = "refreshHandlerPolicy";
    /**
     * {@link JwtContext} 的 jwtIdService 字段名称
     */
    public static final String JWT_ID_SERVICE = "jwtIdService";
    /**
     * {@link JwtContext} 的 jwtCacheTransformService 字段名称
     */
    public static final String JWT_CACHE_TRANSFORM_SERVICE = "jwtCacheTransformService";

    private final RSAPublicKey publicKey;
    private final JWSSigner signer;
    private final JwsAlgorithm jwsAlgorithm;
    private final String kid;
    private final BearerTokenProperties bearerTokenProperties;
    private final RedisConnectionFactory redisConnectionFactory;
    private final JwtBlacklistProperties jwtBlacklistProperties;
    private final JwtRefreshHandlerPolicy refreshHandlerPolicy;
    private final JwtIdService jwtIdService;
    private final JwtCacheTransformService<?> jwtCacheTransformService;
    /**
     * JWT 的有效期
     */
    private final Duration timeout;
    /**
     * JWT 不同服务器间的时钟偏差, 时钟可能存在偏差, 设置时钟偏移量以消除不同服务器间的时钟偏差的影响, 默认: 0 秒.
     * 注意: 此默认值适合 "单服务器" 应用, "多服务器" 应用请更改此值
     */
    private final Duration clockSkew;

    private final OAuth2TokenValidator<Jwt> oAuth2TokenValidator;
    private final MappedJwtClaimSetConverter mappedJwtClaimSetConverter;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public JwtAutoConfiguration(JwtProperties jwtProperties,
                                @Autowired(required = false)
                                RedisConnectionFactory redisConnectionFactory,
                                @Autowired(required = false)
                                @Qualifier("jwtRedisConnectionFactory")
                                RedisConnectionFactory jwtRedisConnectionFactory,
                                @Autowired(required = false) OAuth2ResourceServerProperties auth2ResourceServerProperties,
                                JwtIdService jwtIdService,
                                JwtCacheTransformService<?> jwtCacheTransformService,
                                OAuth2TokenValidator<Jwt> oAuth2TokenValidator,
                                MappedJwtClaimSetConverter mappedJwtClaimSetConverter) throws Exception {

        if (isNull(redisConnectionFactory) && isNull(jwtRedisConnectionFactory)) {
            throw new RuntimeException("redisConnectionFactory 或 jwtRedisConnectionFactory 必须实现一个");
        }

        if (nonNull(jwtRedisConnectionFactory)) {
            this.redisConnectionFactory = jwtRedisConnectionFactory;
        }
        else {
            this.redisConnectionFactory = redisConnectionFactory;
        }

        this.timeout = jwtProperties.getTimeout();
        this.bearerTokenProperties = jwtProperties.getBearer();
        this.jwtBlacklistProperties = jwtProperties.getBlacklist();
        this.refreshHandlerPolicy = jwtProperties.getRefreshHandlerPolicy();
        this.clockSkew = jwtProperties.getClockSkew();
        this.jwtIdService = jwtIdService;
        this.jwtCacheTransformService = jwtCacheTransformService;
        this.oAuth2TokenValidator = oAuth2TokenValidator;
        this.mappedJwtClaimSetConverter = mappedJwtClaimSetConverter;

        Resource resource = jwtProperties.getJksKeyPairLocation();
        if (nonNull(resource)) {
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, jwtProperties.getJksPassword().toCharArray());
            KeyPair keyPair = keyStoreKeyFactory.getKeyPair(jwtProperties.getJksAlias());

            this.publicKey = (RSAPublicKey) keyPair.getPublic();
            this.kid = jwtProperties.getKid();

            RSAKey rsaJwk = (RSAKey) JwtContext.generateJwk(keyPair, this.kid, KeyUse.SIGNATURE);

            // Create RSA-signer with the private key
            this.signer = new RSASSASigner(rsaJwk);
            this.jwsAlgorithm = SignatureAlgorithm.from(jwtProperties.getJwsAlgorithms());
        }
        else if (nonNull(jwtProperties.getMacsSecret())) {

            String macsSecret = jwtProperties.getMacsSecret();
            if (macsSecret.length() < MACS_SECRET_LENGTH) {
                throw new RuntimeException("用于 JWT 的 HMAC protection 的 secret, 字符长度必须大于等于 " + MACS_SECRET_LENGTH);
            }
            // Create HMAC signer
            this.signer = new MACSigner(macsSecret.getBytes(StandardCharsets.UTF_8));
            this.jwsAlgorithm = MacAlgorithm.from(jwtProperties.getJwsAlgorithms());
            this.publicKey = null;
            this.kid = jwtProperties.getKid();
        }
        else {

            if (nonNull(auth2ResourceServerProperties)) {
                OAuth2ResourceServerProperties.Jwt jwt = auth2ResourceServerProperties.getJwt();
                String jwsAlgorithm = jwt.getJwsAlgorithm();
                this.jwsAlgorithm = SignatureAlgorithm.from(jwsAlgorithm);
            }
            else {
                this.jwsAlgorithm = null;
            }
            this.publicKey = null;
            this.signer = null;
            this.kid = null;
        }
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnProperty(prefix = "ums.jwt", name = "expose-refresh-token-uri", havingValue = "true")
    public JwtRefreshTokenController jwtRefreshTokenController(GenerateClaimsSetService generateClaimsSetService,
                                                               UmsUserDetailsService umsUserDetailsService,
                                                               UmsNimbusJwtDecoder jwtDecoder,
                                                               JwtProperties jwtProperties) {
        return new JwtRefreshTokenController(generateClaimsSetService, umsUserDetailsService,
                                             jwtDecoder, jwtProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ums.jwt", name = "expose-jwk-set-uri", havingValue = "true")
    public JwkEndpoint jwkEndpoint(JwtProperties jwtProperties, JwkEndpointPermissionService jwkEndpointPermissionService) {
        requireNonNull(this.publicKey, "jks-key-pair-location cannot bu null");
        requireNonNull(jwkEndpointPermissionService, "jwkEndpointPermissionService cannot bu null");
        return new JwkEndpoint(this.publicKey, jwtProperties.getJwsAlgorithms(),
                               jwkEndpointPermissionService, jwtProperties.getKid());
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @Primary
    public JwtDecoder jwtDecoder(@Autowired(required = false) OAuth2ResourceServerProperties auth2ResourceServerProperties,
                                 @Autowired(required = false) JwkSetUriPassHeaders jwkSetUriPassHeaders,
                                         JwtProperties jwtProperties) {
        Resource jksKeyPairResource = jwtProperties.getJksKeyPairLocation();
        String macsSecret = jwtProperties.getMacsSecret();
        UmsNimbusJwtDecoder jwtDecoder = null;
        if (nonNull(jksKeyPairResource)) {
            jwtDecoder = UmsNimbusJwtDecoder.withPublicKey(this.publicKey,
                                                                jwtProperties.getRefreshHandlerPolicy(),
                                                                jwtProperties.getRemainingRefreshInterval(),
                                                                jwtProperties.getPrincipalClaimName())
                                                 .signatureAlgorithm((SignatureAlgorithm) this.jwsAlgorithm)
                                                 .build();
        }
        else if (hasText(macsSecret)) {
            jwtDecoder =
                    UmsNimbusJwtDecoder.withSecretKey(new SecretKeySpec(macsSecret.getBytes(StandardCharsets.UTF_8),
                                                                        "MAC"),
                                                      jwtProperties.getRefreshHandlerPolicy(),
                                                      jwtProperties.getRemainingRefreshInterval(),
                                                      jwtProperties.getPrincipalClaimName())
                                       .macAlgorithm((MacAlgorithm) this.jwsAlgorithm)
                                       .build();
        }
        else if (nonNull(auth2ResourceServerProperties)) {
            jwtDecoder =
                    UmsNimbusJwtDecoder.withJwkSetUri(auth2ResourceServerProperties.getJwt().getJwkSetUri(),
                                                      jwtProperties.getRefreshHandlerPolicy(),
                                                      jwtProperties.getRemainingRefreshInterval(),
                                                      jwkSetUriPassHeaders,
                                                      jwtProperties.getPrincipalClaimName())
                                       .jwsAlgorithm((SignatureAlgorithm) this.jwsAlgorithm)
                                       .build();
        }

        if (isNull(jwtDecoder)) {
            throw new RuntimeException("未成功创建 org.springframework.security.oauth2.jwt.JwtDecoder; \n" +
                                       "当需要拥有创建 JWT 功能时需要配置 \"ums.jwt.jksKeyPairLocation\" 或 " +
                                               "\"ums.jwt.macsSecret\" 的属性, \n" +
                                       "当仅仅需要解析 JWT 时请配置 \"spring.security.oauth2.resourceserver.jwt.jwk-set-uri\" 属性");
        }

        setJwtValidatorAndClaimSetConverter(oAuth2TokenValidator, mappedJwtClaimSetConverter, jwtDecoder);

        return jwtDecoder;

    }


    @Override
    public void afterPropertiesSet() throws Exception {

        // 注册 JWSSigner signer 到 JwtContext.signer
        Class<JwtContext> jwtUtilClass = JwtContext.class;
        Class.forName(jwtUtilClass.getName());

        if (nonNull(this.signer))
        {
            setFieldValue(SIGNER_PARAM_NAME, this.signer, null, jwtUtilClass);
        }

        if (nonNull(this.jwsAlgorithm)) {
            setFieldValue(JWS_ALGORITHM_PARAM_NAME, this.jwsAlgorithm.getName(), null, jwtUtilClass);
        }

        if (nonNull(this.kid)) {
            setFieldValue(KID_PARAM_NAME, this.kid, null, jwtUtilClass);
        }

        if (nonNull(this.timeout)) {
            setFieldValue(JWT_TIMEOUT, this.timeout, null, jwtUtilClass);
        }

        if (nonNull(this.clockSkew)) {
            setFieldValue(CLOCK_SKEW, this.clockSkew, null, jwtUtilClass);
        }

        if (nonNull(this.bearerTokenProperties)) {
            BearerTokenProperties bearerTokenProperties = new BearerTokenProperties();
            BeanUtils.copyProperties(this.bearerTokenProperties, bearerTokenProperties);
            setFieldValue(BEARER_TOKEN, bearerTokenProperties, null, jwtUtilClass);
        }

        if (nonNull(this.jwtBlacklistProperties)) {
            JwtBlacklistProperties jwtBlacklistProperties = new JwtBlacklistProperties();
            BeanUtils.copyProperties(this.jwtBlacklistProperties, jwtBlacklistProperties);
            setFieldValue(BLACKLIST_PROPERTIES, jwtBlacklistProperties, null, jwtUtilClass);
        }

        if (nonNull(this.redisConnectionFactory)) {
            setFieldValue(REDIS_CONNECTION_FACTORY, this.redisConnectionFactory, null, jwtUtilClass);
        }

        if (nonNull(this.refreshHandlerPolicy)) {
            setFieldValue(REFRESH_HANDLER_POLICY, this.refreshHandlerPolicy, null, jwtUtilClass);
        }

        if (nonNull(this.jwtIdService)) {
            setFieldValue(JWT_ID_SERVICE, this.jwtIdService, null, jwtUtilClass);
        }

        if (nonNull(this.jwtCacheTransformService)) {
            setFieldValue(JWT_CACHE_TRANSFORM_SERVICE, this.jwtCacheTransformService, null, jwtUtilClass);
        }

    }

    private void setJwtValidatorAndClaimSetConverter(@NonNull OAuth2TokenValidator<Jwt> oAuth2TokenValidator,
                                                     @NonNull MappedJwtClaimSetConverter mappedJwtClaimSetConverter,
                                                     @NonNull UmsNimbusJwtDecoder jwtDecoder) {

        // 设置自定义的 JWT Validator
        jwtDecoder.setJwtValidator(oAuth2TokenValidator);
        // 设置自定义的 claim set converter
        jwtDecoder.setClaimSetConverter(mappedJwtClaimSetConverter);
    }

}

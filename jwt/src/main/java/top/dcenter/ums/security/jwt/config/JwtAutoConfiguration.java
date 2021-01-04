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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.jwt.JwtContext;
import top.dcenter.ums.security.jwt.advice.JwtControllerAdvice;
import top.dcenter.ums.security.jwt.api.claims.service.CustomClaimsSetService;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkEndpointPermissionService;
import top.dcenter.ums.security.jwt.api.id.service.JwtIdService;
import top.dcenter.ums.security.jwt.api.supplier.JwtClaimTypeConverterSupplier;
import top.dcenter.ums.security.jwt.api.supplier.JwtGrantedAuthoritiesConverterSupplier;
import top.dcenter.ums.security.jwt.api.validator.service.CustomClaimValidateService;
import top.dcenter.ums.security.jwt.api.validator.service.ReAuthService;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.claims.service.impl.UmsCustomClaimsSetServiceImpl;
import top.dcenter.ums.security.jwt.claims.service.impl.UmsGenerateClaimsSetServiceImpl;
import top.dcenter.ums.security.jwt.controller.JwtRefreshTokenController;
import top.dcenter.ums.security.jwt.decoder.UmsNimbusJwtDecoder;
import top.dcenter.ums.security.jwt.endpoint.JwkEndpoint;
import top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy;
import top.dcenter.ums.security.jwt.factory.KeyStoreKeyFactory;
import top.dcenter.ums.security.jwt.properties.BearerTokenProperties;
import top.dcenter.ums.security.jwt.properties.JwtBlacklistProperties;
import top.dcenter.ums.security.jwt.properties.JwtProperties;
import top.dcenter.ums.security.jwt.resolver.UmsBearerTokenResolver;
import top.dcenter.ums.security.jwt.supplier.UmsJwtClaimTypeConverterSupplier;
import top.dcenter.ums.security.jwt.supplier.UmsJwtGrantedAuthoritiesConverterSupplier;
import top.dcenter.ums.security.jwt.validator.JwtNotBeforeValidator;
import top.dcenter.ums.security.jwt.validator.UmsReAuthServiceImpl;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.CollectionUtils.isEmpty;
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
@AutoConfigureAfter({JwtPropertiesAutoConfiguration.class, JwtIdServiceAutoConfiguration.class,
        RedisSerializerAutoConfiguration.class})
@ConditionalOnProperty(prefix = "ums.jwt", name = "enable", havingValue = "true")
@Slf4j
class JwtAutoConfiguration implements ApplicationListener<ContextRefreshedEvent>, InitializingBean {

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
     * {@link JwtContext} 的 redisSerializer 字段名称
     */
    public static final String REDIS_SERIALIZER = "redisSerializer";
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

    private final RSAPublicKey publicKey;
    private final JWSSigner signer;
    private final JwsAlgorithm jwsAlgorithm;
    private final String kid;
    private final BearerTokenProperties bearerTokenProperties;
    private final RedisConnectionFactory redisConnectionFactory;
    @SuppressWarnings("rawtypes")
    private final RedisSerializer redisSerializer;
    private final JwtBlacklistProperties jwtBlacklistProperties;
    private final JwtRefreshHandlerPolicy refreshHandlerPolicy;
    private final JwtIdService jwtIdService;
    /**
     * JWT 的有效期
     */
    private final Duration timeout;
    /**
     * JWT 不同服务器间的时钟偏差, 时钟可能存在偏差, 设置时钟偏移量以消除不同服务器间的时钟偏差的影响, 默认: 0 秒.
     * 注意: 此默认值适合 "单服务器" 应用, "多服务器" 应用请更改此值
     */
    private final Duration clockSkew;

    private OAuth2TokenValidator<Jwt> oAuth2TokenValidator;
    private MappedJwtClaimSetConverter mappedJwtClaimSetConverter;
    private JwtDecoder jwtDecoder;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private Map<String, CustomClaimValidateService> customClaimValidateServiceMap;

    @SuppressWarnings({"rawtypes"})
    public JwtAutoConfiguration(JwtProperties jwtProperties,
                                RedisConnectionFactory redisConnectionFactory,
                                @Autowired(required = false) OAuth2ResourceServerProperties auth2ResourceServerProperties,
                                @Qualifier("jwtTokenRedisSerializer") RedisSerializer jwtRedisSerializer,
                                JwtIdService jwtIdService) throws Exception {
        this.timeout = jwtProperties.getTimeout();
        this.bearerTokenProperties = jwtProperties.getBearer();
        this.jwtBlacklistProperties = jwtProperties.getBlacklist();
        this.redisConnectionFactory = redisConnectionFactory;
        this.redisSerializer = jwtRedisSerializer;
        this.refreshHandlerPolicy = jwtProperties.getRefreshHandlerPolicy();
        this.clockSkew = jwtProperties.getClockSkew();
        this.jwtIdService = jwtIdService;

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

    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.jwt.api.validator.service.ReAuthService"})
    public ReAuthService reAuthService(JwtProperties jwtProperties) {
        return new UmsReAuthServiceImpl(jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.jwt.advice.JwtControllerAdvice"})
    public JwtControllerAdvice jwtControllerAdvice() {
        return new JwtControllerAdvice();
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

    /**
     * 通过 {@code OAuth2ResourceServerConfigurer.configure(HttpSecurityBuilder)}
     * 配置到 {@code OAuth2ResourceServerConfigurer.BearerTokenRequestMatcher}
     * @param properties    {@link JwtProperties}
     * @return  {@link BearerTokenResolver}
     */
    @Bean
    public BearerTokenResolver bearerTokenResolver(JwtProperties properties) {
        BearerTokenProperties bearer = properties.getBearer();
        String bearerTokenParameterName = bearer.getBearerTokenParameterName();
        String bearerTokenHeaderName = bearer.getBearerTokenHeaderName();
        Boolean allowFormEncodedBodyParameter = bearer.getAllowFormEncodedBodyParameter();
        Boolean allowUriQueryParameter = bearer.getAllowUriQueryParameter();

        if (allowFormEncodedBodyParameter && allowUriQueryParameter) {
            throw new RuntimeException("属性 allowFormEncodedBodyParameter, allowUriQueryParameter 不能同时为 true.");
        }

        if (!hasText(bearerTokenHeaderName) && !hasText(bearerTokenParameterName) ) {
            throw new RuntimeException("属性 bearerTokenHeaderName 或 bearerTokenParameterName 不能是 null 或 空字符串.");
        }

        final UmsBearerTokenResolver bearerTokenResolver =
                new UmsBearerTokenResolver(bearerTokenParameterName, properties.getJwtByRefreshTokenUri());
        bearerTokenResolver.setBearerTokenHeaderName(bearerTokenHeaderName);
        bearerTokenResolver.setAllowFormEncodedBodyParameter(allowFormEncodedBodyParameter);
        bearerTokenResolver.setAllowUriQueryParameter(allowUriQueryParameter);
        return bearerTokenResolver;
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder(OAuth2TokenValidator<Jwt> oAuth2TokenValidator,
                                 MappedJwtClaimSetConverter mappedJwtClaimSetConverter,
                                 @Autowired(required = false) OAuth2ResourceServerProperties auth2ResourceServerProperties,
                                 JwtProperties jwtProperties) {

        Resource jksKeyPairResource = jwtProperties.getJksKeyPairLocation();
        String macsSecret = jwtProperties.getMacsSecret();
        if (nonNull(jksKeyPairResource)) {
            this.jwtDecoder = UmsNimbusJwtDecoder.withPublicKey(this.publicKey,
                                                                jwtProperties.getRefreshHandlerPolicy(),
                                                                jwtProperties.getRemainingRefreshInterval(),
                                                                jwtProperties.getPrincipalClaimName())
                                                 .signatureAlgorithm((SignatureAlgorithm) this.jwsAlgorithm)
                                                 .build();
        }
        else if (hasText(macsSecret)) {
            this.jwtDecoder =
                    UmsNimbusJwtDecoder.withSecretKey(new SecretKeySpec(macsSecret.getBytes(StandardCharsets.UTF_8),
                                                                        "MAC"),
                                                      jwtProperties.getRefreshHandlerPolicy(),
                                                      jwtProperties.getRemainingRefreshInterval(),
                                                      jwtProperties.getPrincipalClaimName())
                                       .build();
        }
        else if (nonNull(auth2ResourceServerProperties)) {
            this.jwtDecoder =
                    UmsNimbusJwtDecoder.withJwkSetUri(auth2ResourceServerProperties.getJwt().getJwkSetUri(),
                                                      jwtProperties.getRefreshHandlerPolicy(),
                                                      jwtProperties.getRemainingRefreshInterval(),
                                                      jwtProperties.getPrincipalClaimName())
                                       .build();
        }

        if (isNull(this.jwtDecoder)) {
            throw new RuntimeException("未成功创建 org.springframework.security.oauth2.jwt.JwtDecoder; \n" +
                                       "当需要拥有创建 JWT 功能时需要配置 \"ums.jwt.jksKeyPairLocation\" 或 " +
                                               "\"ums.jwt.macsSecret\" 的属性, \n" +
                                       "当仅仅需要解析 JWT 时请配置 \"spring.security.oauth2.resourceserver.jwt.jwk-set-uri\" 属性");
        }

        setJwtValidatorAndClaimSetConverter(oAuth2TokenValidator, mappedJwtClaimSetConverter, jwtDecoder);
        return jwtDecoder;

    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter")
    public JwtAuthenticationConverter jwtAuthenticationConverter(JwtProperties jwtProperties,
                                                                 JwtGrantedAuthoritiesConverterSupplier jwtGrantedAuthoritiesConverterSupplier) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName(jwtProperties.getPrincipalClaimName());
        if (nonNull(jwtGrantedAuthoritiesConverterSupplier)) {
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverterSupplier.getConverter());
        }
        return jwtAuthenticationConverter;
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.jwt.converter.factory.JwtClaimTypeConverterSupplier")
    public JwtClaimTypeConverterSupplier jwtClaimTypeConverterSupplier() {
        return new UmsJwtClaimTypeConverterSupplier();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.jwt.api.supplier.JwtGrantedAuthoritiesConverterSupplier")
    public JwtGrantedAuthoritiesConverterSupplier jwtGrantedAuthoritiesConverterSupplier() {
        return new UmsJwtGrantedAuthoritiesConverterSupplier();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService")
    public GenerateClaimsSetService generateClaimsSetService(JwtProperties jwtProperties,
                                                             JwtAuthenticationConverter jwtAuthenticationConverter) {
        return new UmsGenerateClaimsSetServiceImpl(jwtProperties.getTimeout().getSeconds(),
                                                   jwtProperties.getIss(),
                                                   jwtProperties.getPrincipalClaimName(),
                                                   jwtAuthenticationConverter);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.jwt.api.claims.service.CustomClaimsSetService")
    public CustomClaimsSetService customClaimsSetService() {
        return new UmsCustomClaimsSetServiceImpl();
    }

    /**
     * 默认自动通过 {@link NimbusJwtDecoder#setJwtValidator(OAuth2TokenValidator)} 配置
     * @param jwtProperties jwtProperties
     * @return  OAuth2TokenValidator
     */
    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.oauth2.core.OAuth2TokenValidator")
    public OAuth2TokenValidator<Jwt> oAuth2TokenValidator(JwtProperties jwtProperties) {

        final Collection<OAuth2TokenValidator<Jwt>> tokenValidators = new ArrayList<>();
        // exp 校验, 由 UmsNimbusJwtDecoder 内部进行校验, 以便进行 Jwt 的自动续期逻辑
        tokenValidators.add(new JwtNotBeforeValidator(jwtProperties.getClockSkew()));

        // 设置自定义的 JWT Validator
        if (!isEmpty(this.customClaimValidateServiceMap)) {
            this.customClaimValidateServiceMap.values()
                    .forEach(
                        service -> tokenValidators.add(new JwtClaimValidator<>(service.getClaimName(),
                                                                               service::validate))
                    );
        }

        this.oAuth2TokenValidator = new DelegatingOAuth2TokenValidator<>(tokenValidators);

        return this.oAuth2TokenValidator;
    }

    /**
     * 默认自动通过 {@link NimbusJwtDecoder#setClaimSetConverter(Converter)} 配置
     * @param jwtClaimTypeConverterSupplier jwtClaimTypeConverterSupplier
     * @return  MappedJwtClaimSetConverter
     */
    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter")
    public MappedJwtClaimSetConverter mappedJwtClaimSetConverter(JwtClaimTypeConverterSupplier jwtClaimTypeConverterSupplier) {
        // jwt claim set converter
        if (isNull(jwtClaimTypeConverterSupplier)) {
            this.mappedJwtClaimSetConverter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        }
        else {
            this.mappedJwtClaimSetConverter =
                    MappedJwtClaimSetConverter.withDefaults(jwtClaimTypeConverterSupplier.getConverter());
        }

        return this.mappedJwtClaimSetConverter;
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

        if (nonNull(this.redisSerializer)) {
            setFieldValue(REDIS_SERIALIZER, this.redisSerializer, null, jwtUtilClass);
        }

        if (nonNull(this.refreshHandlerPolicy)) {
            setFieldValue(REFRESH_HANDLER_POLICY, this.refreshHandlerPolicy, null, jwtUtilClass);
        }

        if (nonNull(this.jwtIdService)) {
            setFieldValue(JWT_ID_SERVICE, this.jwtIdService, null, jwtUtilClass);
        }

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if (isNull(this.jwtDecoder)) {
            this.jwtDecoder = applicationContext.getBean(NimbusJwtDecoder.class);
            if (isNull(this.oAuth2TokenValidator)) {
                //noinspection unchecked
                this.oAuth2TokenValidator = (OAuth2TokenValidator<Jwt>) applicationContext.getBean(OAuth2TokenValidator.class);
            }
            if (isNull(this.mappedJwtClaimSetConverter)) {
                this.mappedJwtClaimSetConverter = applicationContext.getBean(MappedJwtClaimSetConverter.class);
            }

            // 设置自定义的 JWT Validator 和 claim set converter
            setJwtValidatorAndClaimSetConverter(this.oAuth2TokenValidator,
                                                this.mappedJwtClaimSetConverter,
                                                this.jwtDecoder);
        }

    }

    private void setJwtValidatorAndClaimSetConverter(@NonNull OAuth2TokenValidator<Jwt> oAuth2TokenValidator,
                                                     @NonNull MappedJwtClaimSetConverter mappedJwtClaimSetConverter,
                                                     @NonNull JwtDecoder jwtDecoder) {
        if (jwtDecoder instanceof NimbusJwtDecoder)
        {
            NimbusJwtDecoder decoder = ((NimbusJwtDecoder) jwtDecoder);
            // 设置自定义的 JWT Validator
            decoder.setJwtValidator(oAuth2TokenValidator);
            // 设置自定义的 claim set converter
            decoder.setClaimSetConverter(mappedJwtClaimSetConverter);
        }

        if (jwtDecoder instanceof UmsNimbusJwtDecoder)
        {
            UmsNimbusJwtDecoder decoder = ((UmsNimbusJwtDecoder) jwtDecoder);
            // 设置自定义的 JWT Validator
            decoder.setJwtValidator(oAuth2TokenValidator);
            // 设置自定义的 claim set converter
            decoder.setClaimSetConverter(mappedJwtClaimSetConverter);
        }
    }

}

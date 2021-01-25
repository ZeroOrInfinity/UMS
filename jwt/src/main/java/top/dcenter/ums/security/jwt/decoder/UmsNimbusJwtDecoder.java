/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.dcenter.ums.security.jwt.decoder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.SingleKeyJWSKeySelector;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.jwt.JwtContext;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkSetUriConfig;
import top.dcenter.ums.security.jwt.api.validator.service.ReAuthService;
import top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy;
import top.dcenter.ums.security.jwt.exception.JwtInvalidException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcTraceId;
import static top.dcenter.ums.security.jwt.JwtContext.getClockSkew;

/**
 * 1. 增加 jwt 刷新的处理:<br>
 *   - 因刷新 jwt, 用 oldJwt 失效而添加进黑名单, 值为 newJwt, 此时如果有携带 oldJwt 并发访问请求时,
 *     会自动替换 oldJwt 为 newJwt.<br>
 * 2. 增加是否需要重新登录认证处理.<br>
 * 3. 增加只针对 refreshToken 的 decode 方法, 以提高效率.
 * 4. 增加只针对 刚刷新的 Jwt 无校验的 decode 方法, 以提高效率.
 *
 * A low-level Nimbus implementation of {@link JwtDecoder} which takes a raw Nimbus
 * configuration.
 *
 * @author Josh Cummings
 * @author Joe Grandja
 * @author Mykyta Bezverkhyi
 * @author YongWu zheng
 * @since 5.2
 */
@SuppressWarnings("unused")
@Slf4j
public final class UmsNimbusJwtDecoder implements JwtDecoder {

	private static final String DECODING_ERROR_MESSAGE_TEMPLATE = "An error occurred while attempting to decode the Jwt: %s";

	private final JWTProcessor<SecurityContext> jwtProcessor;
	private final JwtRefreshHandlerPolicy refreshHandlerPolicy;
	/**
	 * 当 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时, JWT 剩余的有效期间隔小于此值后自动刷新 JWT;
	 * 当 {@link JwtRefreshHandlerPolicy#REFRESH_TOKEN} 时, JWT 剩余的有效期间隔小于此值后通过 refreshToken 才会刷新新的 JWT,
	 * 否则直接返回旧的 JWT.
	 */
	@Getter
	private final Duration remainingRefreshInterval;

	private Converter<Map<String, Object>, Map<String, Object>> claimSetConverter = MappedJwtClaimSetConverter
			.withDefaults(Collections.emptyMap());

	private OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefault();

	@Setter
	private ReAuthService reAuthService;

	/**
	 * Configures a {@link UmsNimbusJwtDecoder} with the given parameters
	 * @param jwtProcessor              the {@link JWTProcessor} to use
	 * @param refreshHandlerPolicy      {@link Jwt} 刷新处理策略
	 * @param remainingRefreshInterval  JWT 剩余的有效期间隔小于此值后自动刷新 JWT, 此配置在 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时有效
	 */
	public UmsNimbusJwtDecoder(JWTProcessor<SecurityContext> jwtProcessor,
	                           JwtRefreshHandlerPolicy refreshHandlerPolicy,
	                           Duration remainingRefreshInterval) {
		Assert.notNull(jwtProcessor, "jwtProcessor cannot be null");
		Assert.notNull(refreshHandlerPolicy, "refreshHandlerPolicy cannot be null");
		Assert.notNull(remainingRefreshInterval, "remainingRefreshInterval cannot be null");
		this.refreshHandlerPolicy = refreshHandlerPolicy;
		this.remainingRefreshInterval = remainingRefreshInterval;
		this.jwtProcessor = jwtProcessor;
	}

	/**
	 * Use this {@link Jwt} Validator
	 * @param jwtValidator - the Jwt Validator to use
	 */
	public void setJwtValidator(OAuth2TokenValidator<Jwt> jwtValidator) {
		Assert.notNull(jwtValidator, "jwtValidator cannot be null");
		this.jwtValidator = jwtValidator;
	}

	/**
	 * Use the following {@link Converter} for manipulating the JWT's claim set
	 * @param claimSetConverter the {@link Converter} to use
	 */
	public void setClaimSetConverter(Converter<Map<String, Object>, Map<String, Object>> claimSetConverter) {
		Assert.notNull(claimSetConverter, "claimSetConverter cannot be null");
		this.claimSetConverter = claimSetConverter;
	}

	/**
	 * Decode and validate and refresh the JWT from its compact claims representation format
	 * @param token the JWT value
	 * @return a validated {@link Jwt}
	 * @throws JwtException JwtException
	 */
	@Override
	public Jwt decode(String token) throws JwtException {
		JWT jwt = parse(token);
		if (jwt instanceof PlainJWT) {
			log.trace("Failed to decode unsigned token");
			throw new BadJwtException("Unsupported algorithm of " + jwt.getHeader().getAlgorithm());
		}
		Jwt createdJwt = createJwt(token, jwt);
		// AUTO_RENEW 策略时 前置校验
		if (JwtRefreshHandlerPolicy.AUTO_RENEW.equals(this.refreshHandlerPolicy)) {
			createdJwt = validateJwt(createdJwt);
		}
		if (refreshHandlerPolicy.isRefresh(createdJwt, remainingRefreshInterval,
		                                   getClockSkew(), reAuthService)) {
			createdJwt = refreshHandlerPolicy.refreshHandle(createdJwt, this);
		}
		// 不是 AUTO_RENEW 策略时 后置校验
		if (!JwtRefreshHandlerPolicy.AUTO_RENEW.equals(this.refreshHandlerPolicy)) {
			createdJwt = validateJwt(createdJwt);
		}
		return createdJwt;
	}

	/**
	 * Decode and validate the JWT from its compact claims representation format
	 * @param token the JWT value
	 * @return a validated {@link Jwt}
	 * @throws JwtException JwtException
	 */
	public Jwt decodeNotRefreshToken(String token) throws JwtException {
		JWT jwt = parse(token);
		if (jwt instanceof PlainJWT) {
			log.trace("Failed to decode unsigned token");
			throw new BadJwtException("Unsupported algorithm of " + jwt.getHeader().getAlgorithm());
		}
		Jwt createdJwt = createJwt(token, jwt);
		createdJwt = validateJwt(createdJwt);
		return createdJwt;
	}

	/**
	 * Decode and not validate the JWT from its compact claims representation format
	 * @param token the JWT value
	 * @return a not validated {@link Jwt}
	 * @throws JwtInvalidException  转换 token 到 Jwt 时发生错误
	 */
	public Jwt decodeNotValidate(String token) throws JwtInvalidException {
		try {
			JWT parsedJwt = JWTParser.parse(token);
			// Verify the signature
			JWTClaimsSet jwtClaimsSet = parsedJwt.getJWTClaimsSet();
			Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
			Map<String, Object> claims = this.claimSetConverter.convert(jwtClaimsSet.getClaims());

			requireNonNull(claims, "转换 jwtClaimsSet 到 claims 是返回 null 值");

			// @formatter:off
			return Jwt.withTokenValue(token)
			          .headers((h) -> h.putAll(headers))
			          .claims((c) -> c.putAll(claims))
			          .build();
			// @formatter:on
		}
		catch (Exception ex) {
			log.error("转换 token 到 Jwt 时发生错误", ex);
			throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
		}
	}

	/**
	 * Decode and validate the refresh token JWT from its compact claims representation format
	 * @param token the JWT value
	 * @return a validated {@link Jwt}
	 * @throws JwtInvalidException  转换 token 到 Jwt 时发生错误
	 */
	public Jwt decodeRefreshTokenOfJwt(String token) throws JwtInvalidException {
		try {
			JWT parsedJwt = JWTParser.parse(token);

			// Verify the signature
			JWTClaimsSet jwtClaimsSet = this.jwtProcessor.process(parsedJwt, null);
			Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
			Map<String, Object> claims = this.claimSetConverter.convert(jwtClaimsSet.getClaims());

			requireNonNull(claims, "转换 jwtClaimsSet 到 claims 是返回 null 值");
			// @formatter:off
			Jwt createJwt = Jwt.withTokenValue(token)
					           .headers((h) -> h.putAll(headers))
					           .claims((c) -> c.putAll(claims))
					           .build();
			// @formatter:on
			Instant expiresAt = createJwt.getExpiresAt();
			if (nonNull(expiresAt) && Instant.now().minusSeconds(getClockSkew().getSeconds()).isAfter(expiresAt)) {
				throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
			}

			// 检查是否在黑名单中.
			if (JwtContext.isRefreshJwtInTheBlacklist(createJwt)) {
				throw new JwtInvalidException(ErrorCodeEnum.JWT_REFRESH_TOKEN_INVALID, getMdcTraceId());
			}

			if (reAuthService.isReAuth(createJwt)) {
				JwtContext.addBlacklistForRefreshToken(createJwt);
				throw new JwtInvalidException(ErrorCodeEnum.JWT_RE_AUTH, getMdcTraceId());
			}

			return createJwt;
		}
		catch (JwtInvalidException ex) {
			log.error("因需要重新认证, refresh token 失效", ex);
			throw ex;
		}
		catch (Exception ex) {
			log.error("转换 token 到 Jwt 时发生错误", ex);
			throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
		}
	}

	private JWT parse(String token) {
		try {
			return JWTParser.parse(token);
		}
		catch (Exception ex) {
			log.trace("Failed to parse token", ex);
			throw new BadJwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
		}
	}

	private Jwt createJwt(String token, JWT parsedJwt) {
		try {
			// Verify the signature
			JWTClaimsSet jwtClaimsSet = this.jwtProcessor.process(parsedJwt, null);
			Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
			Map<String, Object> claims = this.claimSetConverter.convert(jwtClaimsSet.getClaims());
			// @formatter:off
			//noinspection ConstantConditions
			return Jwt.withTokenValue(token)
					  .headers((h) -> h.putAll(headers))
					  .claims((c) -> c.putAll(claims))
					  .build();
			// @formatter:on
		}
		catch (RemoteKeySourceException ex) {
			log.trace("Failed to retrieve JWK set", ex);
			if (ex.getCause() instanceof ParseException) {
				throw new JwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, "Malformed Jwk set"));
			}
			throw new JwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
		}
		catch (JOSEException ex) {
			log.trace("Failed to process JWT", ex);
			throw new JwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
		}
		catch (Exception ex) {
			log.trace("Failed to process JWT", ex);
			if (ex.getCause() instanceof ParseException) {
				throw new BadJwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, "Malformed payload"));
			}
			throw new BadJwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
		}
	}

	private Jwt validateJwt(Jwt jwt) {

		// 检查黑名单中是否有刷新 Jwt , 有的话替换.
		Jwt refreshJwt = validateJti(jwt);
		if (!Objects.equals(refreshJwt, jwt)) {
			return refreshJwt;
		}

		OAuth2TokenValidatorResult result = this.jwtValidator.validate(jwt);
		if (result.hasErrors()) {
			Collection<OAuth2Error> errors = result.getErrors();
			String validationErrorString = getJwtValidationExceptionMessage(errors);
			throw new JwtValidationException(validationErrorString, errors);
		}
		return jwt;
	}

	/**
	 * 校验 jwt 的 jti 是否存在黑名单中, 如果存在且带有刷新后的 jwt, 返回刷新后的 jwt.
	 * @param jwt   需要校验的 jwt
	 * @return  原样返回或返回刷新后的 jwt.
	 */
	private Jwt validateJti(Jwt jwt) {
		// 1. 校验 jwt 是否在黑名单, 是否需要重新认证(reAuth)
		JwtContext.BlacklistType blacklistType = JwtContext.jtiInTheBlacklist(jwt);
		try {
	        /* - newJwtString == null 表示不在黑名单中,
	           - newJwtString != null 表示在黑名单中, 但缓存中存储有新的且有效 JWT 则返回新的 jwt 字符串,
	           - 抛出异常表示在黑名单中, 但缓存中没有新的 jwt 字符串, 则需要重新认证.
	         */
			String newJwtString = JwtContext.inBlacklistAndHasNewJwt(blacklistType);

			if (nonNull(newJwtString)) {
				// 在黑名单中, 但缓存中存储有新的且有效 JWT, 返回此 JWT
				return decodeNotValidate(newJwtString);
			}

			// jwt 有效, 原样返回
			return jwt;
		}
		catch (JwtInvalidException e) {
			OAuth2TokenValidatorResult result =
					OAuth2TokenValidatorResult.failure(
							new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
							                "The " + JwtClaimNames.JTI + " claim is not valid",
							                null)
					);
			Collection<OAuth2Error> errors = result.getErrors();
			String validationErrorString = getJwtValidationExceptionMessage(errors);
			throw new JwtValidationException(validationErrorString, errors);
		}

	}

	private String getJwtValidationExceptionMessage(Collection<OAuth2Error> errors) {
		for (OAuth2Error oAuth2Error : errors) {
			if (!StringUtils.isEmpty(oAuth2Error.getDescription())) {
				return String.format(DECODING_ERROR_MESSAGE_TEMPLATE, oAuth2Error.getDescription());
			}
		}
		return "Unable to validate Jwt";
	}

	/**
	 * Use the given <a href="https://tools.ietf.org/html/rfc7517#section-5">JWK Set</a>
	 * uri.
	 * @param jwkSetUri                 the JWK Set uri to use
	 * @param refreshHandlerPolicy      {@link Jwt} 刷新处理策略
	 * @param remainingRefreshInterval  JWT 剩余的有效期间隔小于此值后自动刷新 JWT, 此配置在 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时有效
	 * @param jwkSetUriConfig           用于从 jwk set uri 获取 JWk 时传递 header 的参数
	 * @return a {@link JwkSetUriJwtDecoderBuilder} for further configurations
	 */
	public static JwkSetUriJwtDecoderBuilder withJwkSetUri(String jwkSetUri,
	                                                       JwtRefreshHandlerPolicy refreshHandlerPolicy,
	                                                       Duration remainingRefreshInterval,
	                                                       @Nullable JwkSetUriConfig jwkSetUriConfig) {
		return new JwkSetUriJwtDecoderBuilder(jwkSetUri, refreshHandlerPolicy,
		                                      remainingRefreshInterval,
		                                      jwkSetUriConfig);
	}

	/**
	 * Use the given public key to validate JWTs
	 * @param key                       the public key to use
	 * @param refreshHandlerPolicy      {@link Jwt} 刷新处理策略
	 * @param remainingRefreshInterval  JWT 剩余的有效期间隔小于此值后自动刷新 JWT, 此配置在 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时有效
	 * @return a {@link PublicKeyJwtDecoderBuilder} for further configurations
	 */
	public static PublicKeyJwtDecoderBuilder withPublicKey(RSAPublicKey key,
	                                                       JwtRefreshHandlerPolicy refreshHandlerPolicy,
	                                                       Duration remainingRefreshInterval) {
		return new PublicKeyJwtDecoderBuilder(key, refreshHandlerPolicy, remainingRefreshInterval);
	}

	/**
	 * Use the given {@code SecretKey} to validate the MAC on a JSON Web Signature (JWS).
	 * @param secretKey                 the {@code SecretKey} used to validate the MAC
	 * @param refreshHandlerPolicy      {@link Jwt} 刷新处理策略
	 * @param remainingRefreshInterval  JWT 剩余的有效期间隔小于此值后自动刷新 JWT, 此配置在 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时有效
	 * @return a {@link SecretKeyJwtDecoderBuilder} for further configurations
	 */
	public static SecretKeyJwtDecoderBuilder withSecretKey(SecretKey secretKey,
	                                                       JwtRefreshHandlerPolicy refreshHandlerPolicy,
	                                                       Duration remainingRefreshInterval) {
		return new SecretKeyJwtDecoderBuilder(secretKey, refreshHandlerPolicy, remainingRefreshInterval);
	}

	/**
	 * A builder for creating {@link UmsNimbusJwtDecoder} instances based on a
	 * <a target="_blank" href="https://tools.ietf.org/html/rfc7517#section-5">JWK Set</a>
	 * uri.
	 */
	public static final class JwkSetUriJwtDecoderBuilder {

		private final String jwkSetUri;
		private final JwtRefreshHandlerPolicy refreshHandlerPolicy;
		private final Duration remainingRefreshInterval;
		private final JwkSetUriConfig jwkSetUriConfig;

		@SuppressWarnings("FieldMayBeFinal")
		private Set<SignatureAlgorithm> signatureAlgorithms = new HashSet<>();

		private RestOperations restOperations = new RestTemplate();

		private Cache cache;

		private Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer;

		private JwkSetUriJwtDecoderBuilder(String jwkSetUri,
		                                   JwtRefreshHandlerPolicy refreshHandlerPolicy,
		                                   Duration remainingRefreshInterval,
		                                   @Nullable JwkSetUriConfig jwkSetUriConfig) {
			Assert.hasText(jwkSetUri, "jwkSetUri cannot be empty");
			Assert.notNull(refreshHandlerPolicy, "refreshHandlerPolicy cannot be null");
			Assert.notNull(remainingRefreshInterval, "remainingRefreshInterval cannot be null");
			this.jwkSetUri = jwkSetUri;
			this.refreshHandlerPolicy = refreshHandlerPolicy;
			this.remainingRefreshInterval = remainingRefreshInterval;
			this.jwkSetUriConfig = jwkSetUriConfig;
			this.jwtProcessorCustomizer = (processor) -> {
			};
		}

		/**
		 * Append the given signing
		 * <a href="https://tools.ietf.org/html/rfc7515#section-4.1.1" target=
		 * "_blank">algorithm</a> to the set of algorithms to use.
		 * @param signatureAlgorithm the algorithm to use
		 * @return a {@link JwkSetUriJwtDecoderBuilder} for further configurations
		 */
		public JwkSetUriJwtDecoderBuilder jwsAlgorithm(SignatureAlgorithm signatureAlgorithm) {
			Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
			this.signatureAlgorithms.add(signatureAlgorithm);
			return this;
		}

		/**
		 * Configure the list of
		 * <a href="https://tools.ietf.org/html/rfc7515#section-4.1.1" target=
		 * "_blank">algorithms</a> to use with the given {@link Consumer}.
		 * @param signatureAlgorithmsConsumer a {@link Consumer} for further configuring
		 * the algorithm list
		 * @return a {@link JwkSetUriJwtDecoderBuilder} for further configurations
		 */
		public JwkSetUriJwtDecoderBuilder jwsAlgorithms(Consumer<Set<SignatureAlgorithm>> signatureAlgorithmsConsumer) {
			Assert.notNull(signatureAlgorithmsConsumer, "signatureAlgorithmsConsumer cannot be null");
			signatureAlgorithmsConsumer.accept(this.signatureAlgorithms);
			return this;
		}

		/**
		 * Use the given {@link RestOperations} to coordinate with the authorization
		 * servers indicated in the
		 * <a href="https://tools.ietf.org/html/rfc7517#section-5">JWK Set</a> uri as well
		 * as the <a href=
		 * "https://openid.net/specs/openid-connect-core-1_0.html#IssuerIdentifier">Issuer</a>.
		 * @param restOperations    {@link RestOperations}
		 * @return  {@link JwkSetUriJwtDecoderBuilder}
		 */
		public JwkSetUriJwtDecoderBuilder restOperations(RestOperations restOperations) {
			Assert.notNull(restOperations, "restOperations cannot be null");
			this.restOperations = restOperations;
			return this;
		}

		/**
		 * Use the given {@link Cache} to store
		 * <a href="https://tools.ietf.org/html/rfc7517#section-5">JWK Set</a>.
		 * @param cache the {@link Cache} to be used to store JWK Set
		 * @return a {@link JwkSetUriJwtDecoderBuilder} for further configurations
		 * @since 5.4
		 */
		public JwkSetUriJwtDecoderBuilder cache(Cache cache) {
			Assert.notNull(cache, "cache cannot be null");
			this.cache = cache;
			return this;
		}

		/**
		 * Use the given {@link Consumer} to customize the {@link JWTProcessor
		 * ConfigurableJWTProcessor} before passing it to the build
		 * {@link UmsNimbusJwtDecoder}.
		 * @param jwtProcessorCustomizer the callback used to alter the processor
		 * @return a {@link JwkSetUriJwtDecoderBuilder} for further configurations
		 * @since 5.4
		 */
		public JwkSetUriJwtDecoderBuilder jwtProcessorCustomizer(
				Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer) {
			Assert.notNull(jwtProcessorCustomizer, "jwtProcessorCustomizer cannot be null");
			this.jwtProcessorCustomizer = jwtProcessorCustomizer;
			return this;
		}

		JWSKeySelector<SecurityContext> jwsKeySelector(JWKSource<SecurityContext> jwkSource) {
			if (this.signatureAlgorithms.isEmpty()) {
				return new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
			}
			Set<JWSAlgorithm> jwsAlgorithms = new HashSet<>();
			for (SignatureAlgorithm signatureAlgorithm : this.signatureAlgorithms) {
				JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(signatureAlgorithm.getName());
				jwsAlgorithms.add(jwsAlgorithm);
			}
			return new JWSVerificationKeySelector<>(jwsAlgorithms, jwkSource);
		}

		JWKSource<SecurityContext> jwkSource(ResourceRetriever jwkSetRetriever) {
			if (this.cache == null) {
				return new RemoteJWKSet<>(toURL(this.jwkSetUri), jwkSetRetriever);
			}
			ResourceRetriever cachingJwkSetRetriever = new CachingResourceRetriever(this.cache, jwkSetRetriever);
			if (nonNull(this.jwkSetUriConfig)) {
				return new RemoteJWKSet<>(toURL(this.jwkSetUri), cachingJwkSetRetriever,
				                          new DefaultJWKSetCache(this.jwkSetUriConfig.lifespan(),
				                                                 this.jwkSetUriConfig.refreshTime(),
				                                                 this.jwkSetUriConfig.timeUnit()));
			}
			else {
				return new RemoteJWKSet<>(toURL(this.jwkSetUri), cachingJwkSetRetriever, new DefaultJWKSetCache());
			}
		}

		JWTProcessor<SecurityContext> processor() {
			ResourceRetriever jwkSetRetriever = new RestOperationsResourceRetriever(this.restOperations,
			                                                                        this.jwkSetUriConfig);
			JWKSource<SecurityContext> jwkSource = jwkSource(jwkSetRetriever);
			ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
			jwtProcessor.setJWSKeySelector(jwsKeySelector(jwkSource));
			// Spring Security validates the claim set independent from Nimbus
			jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
			});
			this.jwtProcessorCustomizer.accept(jwtProcessor);
			return jwtProcessor;
		}

		/**
		 * Build the configured {@link UmsNimbusJwtDecoder}.
		 * @return the configured {@link UmsNimbusJwtDecoder}
		 */
		public UmsNimbusJwtDecoder build() {
			return new UmsNimbusJwtDecoder(processor(), refreshHandlerPolicy, remainingRefreshInterval);
		}

		@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
		private static URL toURL(String url) {
			try {
				return new URL(url);
			}
			catch (MalformedURLException ex) {
				throw new IllegalArgumentException("Invalid JWK Set URL \"" + url + "\" : " + ex.getMessage(), ex);
			}
		}

		private static class NoOpJwkSetCache implements JWKSetCache {

			@Override
			public void put(JWKSet jwkSet) {
			}

			@Override
			public JWKSet get() {
				return null;
			}

			@Override
			public boolean requiresRefresh() {
				return true;
			}

		}

		private static class CachingResourceRetriever implements ResourceRetriever {

			private final Cache cache;

			private final ResourceRetriever resourceRetriever;

			CachingResourceRetriever(Cache cache, ResourceRetriever resourceRetriever) {
				this.cache = cache;
				this.resourceRetriever = resourceRetriever;
			}

			@Override
			public Resource retrieveResource(URL url) throws IOException {
				try {
					String jwkSet = this.cache.get(url.toString(),
							() -> this.resourceRetriever.retrieveResource(url).getContent());
					//noinspection ConstantConditions
					return new Resource(jwkSet, "UTF-8");
				}
				catch (Cache.ValueRetrievalException ex) {
					Throwable thrownByValueLoader = ex.getCause();
					if (thrownByValueLoader instanceof IOException) {
						throw (IOException) thrownByValueLoader;
					}
					throw new IOException(thrownByValueLoader);
				}
				catch (Exception ex) {
					throw new IOException(ex);
				}
			}

		}

		private static class RestOperationsResourceRetriever implements ResourceRetriever {

			private static final MediaType APPLICATION_JWK_SET_JSON = new MediaType("application", "jwk-set+json");

			private final RestOperations restOperations;
			private final Map<String, Object> headers;

			RestOperationsResourceRetriever(RestOperations restOperations,
			                                @Nullable JwkSetUriConfig jwkSetUriConfig) {
				Assert.notNull(restOperations, "restOperations cannot be null");
				this.restOperations = restOperations;
				if (nonNull(jwkSetUriConfig)) {
					this.headers = jwkSetUriConfig.headers();
				}
				else {
					this.headers = null;
				}
			}

			@Override
			public Resource retrieveResource(URL url) throws IOException {
				final HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, APPLICATION_JWK_SET_JSON));

				if (nonNull(this.headers)) {
					this.headers.forEach((key, value) -> httpHeaders.set(key, (String) value));
				}

				ResponseEntity<String> response = getResponse(url, httpHeaders);
				if (response.getStatusCodeValue() != 200) {
					throw new IOException(response.toString());
				}
				//noinspection ConstantConditions
				return new Resource(response.getBody(), "UTF-8");
			}

			private ResponseEntity<String> getResponse(URL url, HttpHeaders headers) throws IOException {
				try {
					RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, url.toURI());
					return this.restOperations.exchange(request, String.class);
				}
				catch (Exception ex) {
					throw new IOException(ex);
				}
			}

		}

	}

	/**
	 * A builder for creating {@link UmsNimbusJwtDecoder} instances based on a public key.
	 */
	public static final class PublicKeyJwtDecoderBuilder {

		private JWSAlgorithm jwsAlgorithm;

		private final RSAPublicKey key;
		private final JwtRefreshHandlerPolicy refreshHandlerPolicy;
		private final Duration remainingRefreshInterval;

		private Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer;

		private PublicKeyJwtDecoderBuilder(RSAPublicKey key,
		                                   JwtRefreshHandlerPolicy refreshHandlerPolicy,
		                                   Duration remainingRefreshInterval) {
			Assert.notNull(key, "key cannot be null");
			Assert.notNull(refreshHandlerPolicy, "refreshHandlerPolicy cannot be null");
			Assert.notNull(remainingRefreshInterval, "remainingRefreshInterval cannot be null");
			this.jwsAlgorithm = JWSAlgorithm.RS256;
			this.key = key;
			this.refreshHandlerPolicy = refreshHandlerPolicy;
			this.remainingRefreshInterval = remainingRefreshInterval;
			this.jwtProcessorCustomizer = (processor) -> {
			};
		}

		/**
		 * Use the given signing
		 * <a href="https://tools.ietf.org/html/rfc7515#section-4.1.1" target=
		 * "_blank">algorithm</a>.
		 *
		 * The value should be one of
		 * <a href="https://tools.ietf.org/html/rfc7518#section-3.3" target=
		 * "_blank">RS256, RS384, or RS512</a>.
		 * @param signatureAlgorithm the algorithm to use
		 * @return a {@link PublicKeyJwtDecoderBuilder} for further configurations
		 */
		public PublicKeyJwtDecoderBuilder signatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
			Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
			this.jwsAlgorithm = JWSAlgorithm.parse(signatureAlgorithm.getName());
			return this;
		}

		/**
		 * Use the given {@link Consumer} to customize the {@link JWTProcessor
		 * ConfigurableJWTProcessor} before passing it to the build
		 * {@link UmsNimbusJwtDecoder}.
		 * @param jwtProcessorCustomizer the callback used to alter the processor
		 * @return a {@link PublicKeyJwtDecoderBuilder} for further configurations
		 * @since 5.4
		 */
		public PublicKeyJwtDecoderBuilder jwtProcessorCustomizer(
				Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer) {
			Assert.notNull(jwtProcessorCustomizer, "jwtProcessorCustomizer cannot be null");
			this.jwtProcessorCustomizer = jwtProcessorCustomizer;
			return this;
		}

		JWTProcessor<SecurityContext> processor() {
			Assert.state(JWSAlgorithm.Family.RSA.contains(this.jwsAlgorithm),
					() -> "The provided key is of type RSA; however the signature algorithm is of some other type: "
							+ this.jwsAlgorithm + ". Please indicate one of RS256, RS384, or RS512.");
			JWSKeySelector<SecurityContext> jwsKeySelector = new SingleKeyJWSKeySelector<>(this.jwsAlgorithm, this.key);
			DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
			jwtProcessor.setJWSKeySelector(jwsKeySelector);
			// Spring Security validates the claim set independent from Nimbus
			jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
			});
			this.jwtProcessorCustomizer.accept(jwtProcessor);
			return jwtProcessor;
		}

		/**
		 * Build the configured {@link UmsNimbusJwtDecoder}.
		 * @return the configured {@link UmsNimbusJwtDecoder}
		 */
		public UmsNimbusJwtDecoder build() {
			return new UmsNimbusJwtDecoder(processor(), refreshHandlerPolicy, remainingRefreshInterval);
		}

	}

	/**
	 * A builder for creating {@link UmsNimbusJwtDecoder} instances based on a
	 * {@code SecretKey}.
	 */
	public static final class SecretKeyJwtDecoderBuilder {

		private final SecretKey secretKey;
		private final JwtRefreshHandlerPolicy refreshHandlerPolicy;
		private final Duration remainingRefreshInterval;

		private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

		private Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer;

		private SecretKeyJwtDecoderBuilder(SecretKey secretKey,
		                                   JwtRefreshHandlerPolicy refreshHandlerPolicy,
		                                   Duration remainingRefreshInterval) {
			Assert.notNull(secretKey, "secretKey cannot be null");
			Assert.notNull(refreshHandlerPolicy, "refreshHandlerPolicy cannot be null");
			Assert.notNull(remainingRefreshInterval, "remainingRefreshInterval cannot be null");
			this.secretKey = secretKey;
			this.refreshHandlerPolicy = refreshHandlerPolicy;
			this.remainingRefreshInterval = remainingRefreshInterval;
			this.jwtProcessorCustomizer = (processor) -> {
			};
		}

		/**
		 * Use the given
		 * <a href="https://tools.ietf.org/html/rfc7515#section-4.1.1" target=
		 * "_blank">algorithm</a> when generating the MAC.
		 *
		 * The value should be one of
		 * <a href="https://tools.ietf.org/html/rfc7518#section-3.2" target=
		 * "_blank">HS256, HS384 or HS512</a>.
		 * @param macAlgorithm the MAC algorithm to use
		 * @return a {@link SecretKeyJwtDecoderBuilder} for further configurations
		 */
		public SecretKeyJwtDecoderBuilder macAlgorithm(MacAlgorithm macAlgorithm) {
			Assert.notNull(macAlgorithm, "macAlgorithm cannot be null");
			this.jwsAlgorithm = JWSAlgorithm.parse(macAlgorithm.getName());
			return this;
		}

		/**
		 * Use the given {@link Consumer} to customize the {@link JWTProcessor
		 * ConfigurableJWTProcessor} before passing it to the build
		 * {@link UmsNimbusJwtDecoder}.
		 * @param jwtProcessorCustomizer the callback used to alter the processor
		 * @return a {@link SecretKeyJwtDecoderBuilder} for further configurations
		 * @since 5.4
		 */
		public SecretKeyJwtDecoderBuilder jwtProcessorCustomizer(
				Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer) {
			Assert.notNull(jwtProcessorCustomizer, "jwtProcessorCustomizer cannot be null");
			this.jwtProcessorCustomizer = jwtProcessorCustomizer;
			return this;
		}

		/**
		 * Build the configured {@link UmsNimbusJwtDecoder}.
		 * @return the configured {@link UmsNimbusJwtDecoder}
		 */
		public UmsNimbusJwtDecoder build() {
			return new UmsNimbusJwtDecoder(processor(), refreshHandlerPolicy, remainingRefreshInterval);
		}

		JWTProcessor<SecurityContext> processor() {
			JWSKeySelector<SecurityContext> jwsKeySelector = new SingleKeyJWSKeySelector<>(this.jwsAlgorithm,
					this.secretKey);
			DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
			jwtProcessor.setJWSKeySelector(jwsKeySelector);
			// Spring Security validates the claim set independent from Nimbus
			jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
			});
			this.jwtProcessorCustomizer.accept(jwtProcessor);
			return jwtProcessor;
		}

	}

}

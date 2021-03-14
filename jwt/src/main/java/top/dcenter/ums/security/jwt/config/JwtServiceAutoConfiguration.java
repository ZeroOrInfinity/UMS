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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import top.dcenter.ums.security.common.api.jackson2.SimpleModuleHolder;
import top.dcenter.ums.security.common.api.userdetails.converter.AuthenticationToUserDetailsConverter;
import top.dcenter.ums.security.jwt.advice.JwtControllerAdvice;
import top.dcenter.ums.security.jwt.api.cache.service.JwtCacheTransformService;
import top.dcenter.ums.security.jwt.api.claims.service.CustomClaimsSetService;
import top.dcenter.ums.security.jwt.api.id.service.JwtIdService;
import top.dcenter.ums.security.jwt.api.supplier.JwtClaimTypeConverterSupplier;
import top.dcenter.ums.security.jwt.api.supplier.JwtGrantedAuthoritiesConverterSupplier;
import top.dcenter.ums.security.jwt.api.validator.service.CustomClaimValidateService;
import top.dcenter.ums.security.jwt.api.validator.service.ReAuthService;
import top.dcenter.ums.security.jwt.bearer.UmsBearerTokenAuthenticationEntryPoint;
import top.dcenter.ums.security.jwt.cache.service.UmsJwtCacheTransformServiceImpl;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.claims.service.impl.UmsAuthoritiesClaimsSetServiceImpl;
import top.dcenter.ums.security.jwt.claims.service.impl.UmsGenerateClaimsSetServiceImpl;
import top.dcenter.ums.security.jwt.decoder.UmsNimbusJwtDecoder;
import top.dcenter.ums.security.jwt.id.service.impl.UuidJwtIdServiceImpl;
import top.dcenter.ums.security.jwt.jackson2.JwtJackson2ModuleHolder;
import top.dcenter.ums.security.jwt.properties.BearerTokenProperties;
import top.dcenter.ums.security.jwt.properties.JwtProperties;
import top.dcenter.ums.security.jwt.resolver.UmsBearerTokenResolver;
import top.dcenter.ums.security.jwt.supplier.UmsJwtClaimTypeConverterSupplier;
import top.dcenter.ums.security.jwt.supplier.UmsJwtGrantedAuthoritiesConverterSupplier;
import top.dcenter.ums.security.jwt.userdetails.converter.Oauth2TokenAuthenticationTokenToUserConverter;
import top.dcenter.ums.security.jwt.validator.JwtNotBeforeValidator;
import top.dcenter.ums.security.jwt.validator.UmsReAuthServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;
import static top.dcenter.ums.security.jwt.config.JwtAutoConfiguration.PRINCIPAL_CLAIM_NAME;

/**
 * Jwt 服务自动配置
 * @author YongWu zheng
 * @since 2021.1.1 15:56
 */
@Configuration
@AutoConfigureAfter({RedisSerializerAutoConfiguration.class})
@ConditionalOnProperty(prefix = "ums.jwt", name = "enable", havingValue = "true")
@Slf4j
public class JwtServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(type = {"org.springframework.security.web.AuthenticationEntryPoint"})
    public AuthenticationEntryPoint authenticationEntryPoint(JwtProperties jwtProperties) {
        BearerTokenProperties bearer = jwtProperties.getBearer();
        return new UmsBearerTokenAuthenticationEntryPoint(bearer.getBearerTokenHeaderName(),
                                                          bearer.getBearerTokenParameterName());
    }

    @Bean
    public SimpleModuleHolder jwtJackson2ModuleHolder() {
        return new JwtJackson2ModuleHolder();
    }

    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.common.api.userdetails.converter.AuthenticationToUserDetailsConverter"})
    public AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter() {
        return new Oauth2TokenAuthenticationTokenToUserConverter();
    }

    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.jwt.api.id.service.JwtIdService"})
    public JwtIdService jwtIdService() {
        return new UuidJwtIdServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.jwt.api.cache.service.JwtCacheTransformService"})
    public JwtCacheTransformService<?> jwtCacheTransformService(RedisSerializer<JwtAuthenticationToken> redisSerializer) {
        return new UmsJwtCacheTransformServiceImpl(redisSerializer);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.jwt.api.claims.service.CustomClaimsSetService")
    public CustomClaimsSetService customClaimsSetService() {
        return new UmsAuthoritiesClaimsSetServiceImpl();
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
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.jwt.api.supplier.JwtGrantedAuthoritiesConverterSupplier")
    public JwtGrantedAuthoritiesConverterSupplier jwtGrantedAuthoritiesConverterSupplier() {
        return new UmsJwtGrantedAuthoritiesConverterSupplier();
    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter")
    public JwtAuthenticationConverter jwtAuthenticationConverter(JwtProperties jwtProperties,
                                                                 JwtGrantedAuthoritiesConverterSupplier jwtGrantedAuthoritiesConverterSupplier) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        try {
            // 增加对 springBoot 高版本的 JwtAuthenticationConverter 兼容性
            jwtAuthenticationConverter.getClass().getDeclaredField(PRINCIPAL_CLAIM_NAME);

            jwtAuthenticationConverter.setPrincipalClaimName(jwtProperties.getPrincipalClaimName());
        }
        catch (NoSuchFieldException e) {
            log.info("ums.jwt.principalClaimName must be sub");
        }
        if (nonNull(jwtGrantedAuthoritiesConverterSupplier)) {
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverterSupplier.getConverter());
        }
        return jwtAuthenticationConverter;
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.jwt.api.supplier.JwtClaimTypeConverterSupplier")
    public JwtClaimTypeConverterSupplier jwtClaimTypeConverterSupplier() {
        return new UmsJwtClaimTypeConverterSupplier();
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

    /**
     * 默认自动通过 {@link UmsNimbusJwtDecoder#setClaimSetConverter(Converter)} 配置
     * @param jwtClaimTypeConverterSupplier jwtClaimTypeConverterSupplier
     * @return  MappedJwtClaimSetConverter
     */
    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter")
    public MappedJwtClaimSetConverter mappedJwtClaimSetConverter(JwtClaimTypeConverterSupplier jwtClaimTypeConverterSupplier) {
        MappedJwtClaimSetConverter mappedJwtClaimSetConverter;
        // jwt claim set converter
        if (isNull(jwtClaimTypeConverterSupplier)) {
            mappedJwtClaimSetConverter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        }
        else {
            mappedJwtClaimSetConverter =
                    MappedJwtClaimSetConverter.withDefaults(jwtClaimTypeConverterSupplier.getConverter());
        }

        return mappedJwtClaimSetConverter;
    }

    /**
     * 默认自动通过 {@link UmsNimbusJwtDecoder#setJwtValidator(OAuth2TokenValidator)} 配置
     * @param jwtProperties jwtProperties
     * @return  OAuth2TokenValidator
     */
    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.oauth2.core.OAuth2TokenValidator")
    public OAuth2TokenValidator<Jwt> oAuth2TokenValidator(JwtProperties jwtProperties,
                                                          @Autowired(required = false)
                                                          Map<String, CustomClaimValidateService> customClaimValidateServiceMap) {

        final Collection<OAuth2TokenValidator<Jwt>> tokenValidators = new ArrayList<>();
        // exp 校验, 由 UmsNimbusJwtDecoder 内部进行校验, 以便进行 Jwt 的自动续期逻辑
        tokenValidators.add(new JwtNotBeforeValidator(jwtProperties.getClockSkew()));

        // 设置自定义的 JWT Validator
        if (!isEmpty(customClaimValidateServiceMap)) {
            customClaimValidateServiceMap.values()
                                              .forEach(
                                                      service -> tokenValidators.add(new JwtClaimValidator<>(service.getClaimName(),
                                                                                                             service::validate))
                                              );
        }

        return new DelegatingOAuth2TokenValidator<>(tokenValidators);
    }

}

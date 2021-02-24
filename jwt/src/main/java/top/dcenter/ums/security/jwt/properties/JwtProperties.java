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
package top.dcenter.ums.security.jwt.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkEndpointPermissionService;
import top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * jwt 属性
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.5 14:28
 */
@SuppressWarnings("jol")
@ConfigurationProperties(prefix = "ums.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Bearer token 属性
     */
    @NestedConfigurationProperty
    public final BearerTokenProperties bearer = new BearerTokenProperties();

    /**
     * jti 缓存(用于 refreshToken), 以及 jwt 黑名单缓存属性(旧 jwt 失效引发的并发访问问题).
     */
    @NestedConfigurationProperty
    public final JwtBlacklistProperties blacklist = new JwtBlacklistProperties();

    // ================= JWT 相关 =================

    /**
     * 用于 JWT 的 HMAC protection 的 secret, 字符长度必须大于等于 32
     */
    public static final int MACS_SECRET_LENGTH = 32;

    /**
     * 是否支持 jwt, 默认: false
     */
    private Boolean enable = Boolean.FALSE;

    /**
     * JWT 的有效期, 默认: 1 小时
     */
    private Duration timeout = Duration.ofHours(1);

    /**
     * 授权服务器的时钟与资源服务器的时钟可能存在偏差, 设置时钟偏移量以消除不同服务器间的时钟偏差的影响, 默认: 0 秒.<br>
     * 注意: 此默认值适合 "单服务器" 应用, "多服务器" 应用请更改此值
     */
    private Duration clockSkew = Duration.ofSeconds(0);

    /**
     * 当 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时, JWT 剩余的有效期间隔小于此值后自动刷新 JWT;
     * 当 {@link JwtRefreshHandlerPolicy#REFRESH_TOKEN} 时, JWT 剩余的有效期间隔小于此值后通过 refreshToken 才会刷新新的 JWT,
     * 否则直接返回旧的 JWT. 默认: 600 秒.
     */
    private Duration remainingRefreshInterval = Duration.ofSeconds(600);

    /**
     * 通过 refreshToken 刷新 jwt 时, 如果 alwaysRefresh = false, oldJwt 剩余有效期没在 ums.jwt.remainingRefreshInterval 的时间内, 原样返回
     * oldJwt, 如果 ums.jwt.alwaysRefresh = true, 每次通过 refreshToken 刷新 jwt 则总是返回 newJwt.
     * 默认: false
     */
    private Boolean alwaysRefresh = Boolean.FALSE;

    /**
     * {@link Jwt} 刷新处理策略, 默认: REJECT .<br>
     */
    private JwtRefreshHandlerPolicy refreshHandlerPolicy = JwtRefreshHandlerPolicy.REJECT;

    /**
     * 通过 refreshToken 获取 JWT uri, 默认: /jwt/refreshToken,
     * 注意: 当 exposeRefreshTokenUri=true 时才生效
     */
    private String jwtByRefreshTokenUri = "/jwt/refreshToken";

    /**
     * 是否曝露 jwtByRefreshTokenUri, 默认: false
     */
    private Boolean exposeRefreshTokenUri = Boolean.FALSE;


    // ================= JWS 与 JWK 相关 =================

    /**
     * 是否曝露 jwk-set-uri, 默认: false.
     * 如果为 true, 需要实现 {@link JwkEndpointPermissionService} 权限服务接口
     */
    private Boolean exposeJwkSetUri = Boolean.FALSE;

    /**
     * Use the given signing algorithm . The value should be one of RS256, RS384, RS512, HS256, HS384, or HS512 .
     * 默认: RS256
     */
    private String jwsAlgorithms = JwsAlgorithms.RS256;
    /**
     * 用于 JWT 的 HMAC protection 的 secret, 字符长度必须大于等于 32; 当设置此属性时, 属性 jwsAlgorithms
     * 必须是 HS256, HS384, HS512 中的一个.<br>
     * 注意: 属性 macsSecret 与 jksKeyPairLocation 同时配置, jksKeyPairLocation 属性优于 macsSecret.
     */
    private String macsSecret;
    /**
     * 用于 JWT 的密钥键值对(KeyPair)的文件位置; 当设置此属性时, 属性 jwsAlgorithms 必须是 RS256, RS384, RS512 中的一个.<br>
     * 注意: 属性 macsSecret 与 jksKeyPairLocation 同时配置, jksKeyPairLocation 属性优于 macsSecret.
     */
    private Resource jksKeyPairLocation;
    /**
     * 密钥键值对(KeyPair)的别名
     */
    private String jksAlias;
    /**
     * 密钥键值对(KeyPair)的密码
     */
    private String jksPassword;

    // ================= ClaimSet 相关 =================
    /**
     * JWT 存储 principal 的 claimName, 默认: sub .<br>
     * 注意: 如果是高版本的 {@link JwtAuthenticationConverter}, 高版本没有 {@code principalClaimName} 字段, 所以必须是此默认值.
     */
    private String principalClaimName = JwtClaimNames.SUB;

    /**
     *   The "kid" (key ID) parameter is used to match a specific key.  This
     *   is used, for instance, to choose among a set of keys within a JWK Set
     *   during key rollover.  The structure of the "kid" value is
     *   unspecified.  When "kid" values are used within a JWK Set, different
     *   keys within the JWK Set SHOULD use distinct "kid" values.  (One
     *   example in which different keys might use the same "kid" value is if
     *   they have different "kty" (key type) values but are considered to be
     *   equivalent alternatives by the application using them.)  The "kid"
     *   value is a case-sensitive string.  Use of this member is OPTIONAL.
     *   When used with JWS or JWE, the "kid" value is used to match a JWS or
     *   JWE "kid" Header Parameter value.
     */
    private String kid;

    /**
     * 该JWT的签发者, 必须是 URL, 根据是否需要设置
     */
    private String iss;

    // ==================================================

    public String readJksKeyPair() throws IOException {
        String key = "ums.jwt.jks-key-pair-location";
        Assert.notNull(this.jksKeyPairLocation, "jksKeyPairLocation must not be null");
        if (!this.jksKeyPairLocation.exists()) {
            throw new InvalidConfigurationPropertyValueException(key, this.jksKeyPairLocation,
                                                                 "jks key location does not exist");
        }
        try (InputStream inputStream = this.jksKeyPairLocation.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}

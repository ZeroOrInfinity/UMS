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
package top.dcenter.ums.security.jwt.api.endpoind.service;

import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用于从 jwk set uri 获取 JWk 时传递 header,
 * 获取 jwk set 后缓存的时间, 访问 jwk set uri 的频率 的参数接口, 通过{@code UmsNimbusJwtDecoder.RestOperationsResourceRetriever}
 * 传递 header 参数.<br>
 * 不是必须实现的接口, 可以与 {@link JwkEndpointPermissionService} 配合使用.
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.19 14:23
 */
public interface JwkSetUriConfig {

    /**
     * 用于从 jwk set uri 获取 JWk 时传递 header 的参数
     * @return  header 的参数
     */
    @NonNull
    Map<String, Object> headers();


    /**
     * The lifespan of the cached JWK set, in {@link #timeUnit}s, negative
     * means no expiration.
     * @return 获取 jwk set 后缓存的时间
     */
    default long lifespan() {
        return DefaultJWKSetCache.DEFAULT_LIFESPAN_MINUTES;
    }

    /**
     * The refresh time of the cached JWK set, in {@link #timeUnit}s,
     * negative means no refresh time.
     * @return 访问 jwk set uri 的频率
     */
    default long refreshTime() {
        return DefaultJWKSetCache.DEFAULT_REFRESH_TIME_MINUTES;
    }


    /**
     * The time unit, may be {@code null} if no expiration / refresh time.
     * @return {@link #lifespan()} 与 {@link #refreshTime()} 的时间单位
     */
    @NonNull
    default TimeUnit timeUnit() {
        return TimeUnit.MINUTES;
    }
}

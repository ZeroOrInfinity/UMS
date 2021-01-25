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
package top.dcenter.ums.security.jwt.handler;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;
import top.dcenter.ums.security.jwt.decoder.UmsNimbusJwtDecoder;
import top.dcenter.ums.security.jwt.enums.JwtRefreshHandlerPolicy;
import top.dcenter.ums.security.jwt.exception.JwtExpiredException;
import top.dcenter.ums.security.jwt.exception.JwtInvalidException;
import top.dcenter.ums.security.jwt.api.validator.service.ReAuthService;
import top.dcenter.ums.security.jwt.exception.JwtReAuthException;

import java.time.Duration;

/**
 * {@link Jwt} 刷新处理器
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.15 11:27
 */
public interface JwtRefreshHandler {

    /**
     * {@link Jwt} 是否需要刷新
     *
     * @param jwt                      {@link Jwt}
     * @param remainingRefreshInterval jwt 剩余的有效期间隔小于此值则表示需要刷新 jwt,
     *                                 当 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时需要使用此参数,
     *                                 当 {@link JwtRefreshHandlerPolicy#REFRESH_TOKEN}
     *                                 {@link JwtRefreshHandlerPolicy#REJECT} 时则不需要使用此参数.
     * @param reAuthService            用于刷新 Jwt 有效期前校验是否需要用户重新登录的服务
     * @param clockSkew                Jwt 不同服务器间的时钟偏差, 通过 {@code ums.jwt.clockSkew} 设置.
     * @return 返回 true 时表示需要刷新 {@link Jwt}
     * @throws JwtInvalidException Jwt 格式错误 或 需要重新认证
     * @throws JwtReAuthException  Jwt 需要重新登录认证, refreshToken 也一起失效
     */
    @NonNull
    Boolean isRefresh(@NonNull Jwt jwt, @NonNull Duration remainingRefreshInterval,
                      @NonNull Duration clockSkew, @Nullable ReAuthService reAuthService)
            throws JwtInvalidException, JwtReAuthException;

    /**
     * 刷新 Jwt 处理器, 当 jwt 刷新策略为 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时, 刷新的 jwt 直接设置到 header 中,
     * 前端可以从相应的 header 中获取.
     * @param jwt                   过期 或 需要刷新的 {@link Jwt}
     * @param jwtDecoder            {@link UmsNimbusJwtDecoder}
     * @return  返回新的 {@link Jwt}
     * @throws JwtExpiredException  Jwt 过期 异常
     * @throws JwtInvalidException  Jwt 失效 异常
     */
    @NonNull
    Jwt refreshHandle(@NonNull Jwt jwt, @NonNull UmsNimbusJwtDecoder jwtDecoder) throws JwtExpiredException, JwtInvalidException;

}

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
package top.dcenter.ums.security.jwt.enums;

import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.jwt.JwtContext;
import top.dcenter.ums.security.jwt.api.validator.service.ReAuthService;
import top.dcenter.ums.security.jwt.decoder.UmsNimbusJwtDecoder;
import top.dcenter.ums.security.jwt.exception.JwtExpiredException;
import top.dcenter.ums.security.jwt.exception.JwtInvalidException;
import top.dcenter.ums.security.jwt.exception.JwtReAuthException;
import top.dcenter.ums.security.jwt.handler.JwtRefreshHandler;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcTraceId;

/**
 * {@link Jwt} 刷新处理策略
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.15 11:20
 */
@Slf4j
public enum JwtRefreshHandlerPolicy implements JwtRefreshHandler {
    /**
     * 自动续期, {@link Jwt} 过期自动续期策略, 刷新的 jwt 直接设置到 header 中, 前端可以从相应的 header 中获取.
     */
    AUTO_RENEW {

        @Override
        @NonNull
        public Boolean isRefresh(@NonNull Jwt jwt, @NonNull Duration remainingRefreshInterval,
                                 @NonNull Duration clockSkew, @Nullable ReAuthService reAuthService,
                                 @NonNull Boolean isReAuth) throws JwtInvalidException, JwtReAuthException {
            Instant expiresAt = check(jwt, reAuthService, isReAuth);
            long nowOfClockShew = Instant.now().minusSeconds(clockSkew.getSeconds()).getEpochSecond();
            long remainingSecond = expiresAt.getEpochSecond() - nowOfClockShew;
            if (remainingSecond < 0) {
                throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
            }
            return remainingSecond < remainingRefreshInterval.getSeconds();
        }

        @Override
        @NonNull
        public Jwt refreshHandle(@NonNull Jwt jwt, @NonNull UmsNimbusJwtDecoder jwtDecoder) throws JwtInvalidException  {
            Jwt resetJwt;
            try {
                resetJwt = JwtContext.resetJwtExpOfAutoRenewPolicy(jwt, jwtDecoder, this);
            }
            catch (ParseException | JOSEException e) {
                log.error(e.getMessage(), e);
                try {
                    resetJwt = JwtContext.resetJwtExpOfAutoRenewPolicy(jwt, jwtDecoder, this);
                }
                catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
                }
            }
            return resetJwt;
        }
    },
    /**
     *  {@link Jwt} 过期, 直接抛出 {JwtExpiredException}, 由前端通过 refresh token 来刷新 {@link Jwt}.
     */
    REFRESH_TOKEN {

        @Override
        @NonNull
        public Boolean isRefresh(@NonNull Jwt jwt, @NonNull Duration remainingRefreshInterval,
                                 @NonNull Duration clockSkew, @Nullable ReAuthService reAuthService,
                                 @NonNull Boolean isReAuth) throws JwtInvalidException, JwtReAuthException {
            Instant expiresAt = check(jwt, reAuthService, isReAuth);
            return Instant.now().minusSeconds(clockSkew.getSeconds()).isAfter(expiresAt);
        }

        @Override
        @NonNull
        public Jwt refreshHandle(@NonNull Jwt jwt, @NonNull UmsNimbusJwtDecoder jwtDecoder) throws JwtExpiredException {
            throw new JwtExpiredException(ErrorCodeEnum.JWT_EXPIRED, getMdcTraceId());
        }
    },
    /**
     * {@link Jwt} 过期直接抛出 {@link JwtInvalidException}, 需要重新认证来获取新的 {@link Jwt}, 如果未过期直接原样返回.
     */
    REJECT {

        @Override
        @NonNull
        public Boolean isRefresh(@NonNull Jwt jwt, @NonNull Duration remainingRefreshInterval,
                                 @NonNull Duration clockSkew, @Nullable ReAuthService reAuthService,
                                 @NonNull Boolean isReAuth) throws JwtInvalidException, JwtReAuthException {
            Instant expiresAt = check(jwt, reAuthService, isReAuth);
            return Instant.now().minusSeconds(clockSkew.getSeconds()).isAfter(expiresAt);
        }

        @Override
        @NonNull
        public Jwt refreshHandle(@NonNull Jwt jwt, @NonNull UmsNimbusJwtDecoder jwtDecoder) throws JwtInvalidException {
            //  Jwt 过期直接抛出 JwtInvalidException.<br>
            throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
        }
    };

    /**
     * 检查 {@link Jwt} 的有效性及是否需要重新认证.
     * @param jwt                   {@link Jwt}
     * @param reAuthService         {@link ReAuthService}
     * @param isReAuth              是否需要重新认证检查
     * @return 返回 {@link Jwt} 的过期时间
     * @throws JwtInvalidException Jwt 格式错误 或 需要重新认证
     */
    @NonNull
    private static Instant check(@NonNull Jwt jwt, @Nullable ReAuthService reAuthService, @NonNull Boolean isReAuth)
            throws JwtInvalidException, JwtReAuthException {
        Instant expiresAt = jwt.getExpiresAt();
        if (isNull(expiresAt)) {
            throw new JwtInvalidException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
        }

        if (isReAuth && nonNull(reAuthService) && reAuthService.isReAuth(jwt)) {
            // 添加黑名单
            JwtContext.addBlacklistForReAuth(jwt);
            throw new JwtReAuthException(ErrorCodeEnum.JWT_INVALID, getMdcTraceId());
        }
        return expiresAt;
    }


}

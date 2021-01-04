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
package top.dcenter.ums.security.jwt.claims.service.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.jwt.api.claims.service.CustomClaimsSetService;
import top.dcenter.ums.security.jwt.api.id.service.JwtIdService;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames;

import java.time.Instant;

import static java.util.Objects.nonNull;

/**
 * 根据 {@link Authentication} 生成 {@link JWTClaimsSet} 的接口简单的实现.<br>
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.9 22:24
 */
@Slf4j
public class UmsGenerateClaimsSetServiceImpl implements GenerateClaimsSetService {

    /**
     * jwt 的有效期, 单位: 秒
     */
    private final long timeout;
    /**
     * jwt 的 issue
     */
    private final String iss;
    /**
     * JWT 存储 principal 的 claimName
     */
    private final String principalClaimName;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    private TenantContextHolder tenantContextHolder;
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    private CustomClaimsSetService customClaimsSetService;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private JwtIdService jwtIdService;

    /**
     * @param timeout                       jwt 的有效期, 单位: 秒
     * @param iss                           {@link JwtClaimNames#ISS}
     * @param principalClaimName            JWT 存储 principal 的 claimName
     * @param jwtAuthenticationConverter    {@link JwtAuthenticationConverter}
     */
    public UmsGenerateClaimsSetServiceImpl(@NonNull long timeout, @Nullable String iss,
                                           @NonNull String principalClaimName,
                                           @NonNull JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.timeout = timeout;
        this.iss = iss;
        this.principalClaimName = principalClaimName;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @NonNull
    @Override
    public JWTClaimsSet generateClaimsSet(@NonNull UserDetails userDetails, @Nullable Jwt refreshTokenJwt) {

        String tenantId = null;
        if (nonNull(tenantContextHolder)) {
            tenantId = tenantContextHolder.getTenantId(userDetails);
        }

        // Prepare JWT with claims set
        final JWTClaimsSet.Builder builder = getJwtClaimsSetBuilder(tenantId,
                                                                    userDetails.getUsername(),
                                                                    refreshTokenJwt);

        if (nonNull(refreshTokenJwt)) {
            builder.claim(JwtCustomClaimNames.REFRESH_TOKEN_JTI.getClaimName(), refreshTokenJwt.getId());
        }

        if (nonNull(customClaimsSetService)) {
            JWTClaimsSet jwtClaimsSet = customClaimsSetService.toClaimsSet(userDetails);
            jwtClaimsSet.getClaims().forEach(builder::claim);
        }

        return builder.build();
    }

    @Override
    @NonNull
    public JWTClaimsSet generateClaimsSet(@NonNull Authentication authentication, @Nullable Jwt refreshTokenJwt) {

        String tenantId = null;
        if (nonNull(tenantContextHolder)) {
            tenantId = tenantContextHolder.getTenantId(authentication);
        }

        // Prepare JWT with claims set
        final JWTClaimsSet.Builder builder = getJwtClaimsSetBuilder(tenantId,
                                                                    authentication.getName(),
                                                                    refreshTokenJwt);

        if (nonNull(customClaimsSetService)) {
            JWTClaimsSet jwtClaimsSet = customClaimsSetService.toClaimsSet(authentication);
            jwtClaimsSet.getClaims().forEach(builder::claim);
        }

        return builder.build();
    }

    @Override
    @NonNull
    public String getPrincipalClaimName() {
        return this.principalClaimName;
    }

    @Override
    @NonNull
    public JwtAuthenticationConverter getJwtAuthenticationConverter() {
        return this.jwtAuthenticationConverter;
    }


    private JWTClaimsSet.Builder getJwtClaimsSetBuilder(@Nullable String tenantId, @NonNull String userId,
                                                        @Nullable Jwt refreshTokenJwt) {
        // Prepare JWT with claims set
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        // tenantId
        if (nonNull(tenantId)) {
            builder.claim(JwtCustomClaimNames.TENANT_ID.getClaimName(), tenantId);
        }

        // iss
        if (nonNull(iss)) {
            builder.issuer(this.iss);
        }

        // jti
        builder.jwtID(this.jwtIdService.generateJtiId());
        builder.claim(this.principalClaimName, userId)
               .claim(JwtClaimNames.EXP, Instant.now().plusSeconds(timeout).getEpochSecond());

        if (nonNull(refreshTokenJwt)) {
            builder.claim(JwtCustomClaimNames.REFRESH_TOKEN_JTI.getClaimName(), refreshTokenJwt.getId());
        }

        return builder;
    }

}

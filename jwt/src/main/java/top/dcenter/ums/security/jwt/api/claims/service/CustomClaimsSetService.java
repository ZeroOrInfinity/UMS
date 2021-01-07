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
package top.dcenter.ums.security.jwt.api.claims.service;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames;
import top.dcenter.ums.security.jwt.properties.JwtProperties;
import top.dcenter.ums.security.jwt.supplier.UmsJwtGrantedAuthoritiesConverterSupplier;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 根据 {@link Authentication} 生成自定义的 {@link JWTClaimsSet} 的接口, 此接口最终回被
 * {@link GenerateClaimsSetService#generateClaimsSet(Authentication, Jwt)} 或
 * {@link GenerateClaimsSetService#generateClaimsSet(UserDetails, Jwt)} 方法调用.
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.9 22:14
 */
public interface CustomClaimsSetService {

    /**
     * 根据 {@link Authentication} 生成自定义的 {@link JWTClaimsSet}. <br>
     * {@link GenerateClaimsSetService#generateClaimsSet(Authentication, Jwt)} 已生成
     * {@link JwtClaimNames#JTI}, {@link JwtClaimNames#ISS}, {@link JwtClaimNames#EXP},
     * {@link JwtCustomClaimNames#TENANT_ID}, {@link JwtProperties#getPrincipalClaimName()},
     * {@link JwtCustomClaimNames#AUTHORITIES} , 通过此方法生成上述 Claims, 可以覆盖
     * {@link JwtClaimNames#JTI}, {@link JwtClaimNames#ISS}, {@link JwtClaimNames#EXP},
     * {@link JwtCustomClaimNames#TENANT_ID}, {@link JwtProperties#getPrincipalClaimName()},
     * {@link JwtCustomClaimNames#AUTHORITIES} 的值.<br>
     * 注意: {@link JWTClaimsSet} 中的"日期"都以"时间戳"表示且"时间戳"以秒为单位
     * @param authentication    authentication
     * @return  返回 {@link JWTClaimsSet}
     */
    @NonNull
    JWTClaimsSet toClaimsSet(@NonNull Authentication authentication);

    /**
     * 根据 {@link UserDetails} 生成自定义的 {@link JWTClaimsSet}. <br>
     * {@link GenerateClaimsSetService#generateClaimsSet(UserDetails, Jwt)} 已生成
     * {@link JwtClaimNames#JTI}, {@link JwtClaimNames#ISS}, {@link JwtClaimNames#EXP},
     * {@link JwtCustomClaimNames#TENANT_ID}, {@link JwtProperties#getPrincipalClaimName()},
     * {@link JwtCustomClaimNames#AUTHORITIES} , 通过此方法生成上述 Claims, 可以覆盖
     * {@link JwtClaimNames#JTI}, {@link JwtClaimNames#ISS}, {@link JwtClaimNames#EXP},
     * {@link JwtCustomClaimNames#TENANT_ID}, {@link JwtProperties#getPrincipalClaimName()},
     * {@link JwtCustomClaimNames#AUTHORITIES} 的值.<br>
     * 注意: {@link JWTClaimsSet} 中的"日期"都以"时间戳"表示且"时间戳"以秒为单位
     * @param userDetails    {@link UserDetails}
     * @return  返回 {@link JWTClaimsSet}
     */
    @NonNull
    JWTClaimsSet toClaimsSet(@NonNull UserDetails userDetails);

    /**
     * 根据权限集合 authorities 生成 {@link JWTClaimsSet.Builder}. 此实现与 {@link UmsJwtGrantedAuthoritiesConverterSupplier}
     * 有关联的. 也就是说, 覆写此默认实现需要同时替换 {@link UmsJwtGrantedAuthoritiesConverterSupplier} 逻辑.
     * @param authorities   权限集合
     * @return  返回 {@link JWTClaimsSet.Builder}
     */
    @NonNull
    default JWTClaimsSet.Builder getJwtClaimsSetBuilderWithAuthorities(@NonNull Collection<? extends GrantedAuthority> authorities) {
        // Prepare JWT with claims set
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        // 转换为权限字符串
        String authoritiesString = authorities.stream()
                                              .map(GrantedAuthority::getAuthority)
                                              .collect(Collectors.joining(" "));
        // 设置权限
        builder.claim(JwtCustomClaimNames.AUTHORITIES.getClaimName(), authoritiesString);

        return builder;
    }
}

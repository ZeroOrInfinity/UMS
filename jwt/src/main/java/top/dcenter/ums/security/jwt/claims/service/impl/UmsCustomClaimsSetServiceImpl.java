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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import top.dcenter.ums.security.jwt.api.claims.service.CustomClaimsSetService;
import top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames;
import top.dcenter.ums.security.jwt.supplier.UmsJwtGrantedAuthoritiesConverterSupplier;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 根据 {@link Authentication} 生成 {@link JwtCustomClaimNames#AUTHORITIES} 权限的 {@link JWTClaimsSet} 的接口简单的实现.<br>
 * 注意: 替换此 {@link JwtCustomClaimNames#AUTHORITIES} 实现时请同时替换 {@link UmsJwtGrantedAuthoritiesConverterSupplier} 实现.
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.20 19:44
 */
public class UmsCustomClaimsSetServiceImpl implements CustomClaimsSetService {

    @Override
    public JWTClaimsSet toClaimsSet(Authentication authentication) {

        // Prepare JWT with claims set
        JWTClaimsSet.Builder builder = getJwtClaimsSetBuilder(authentication.getAuthorities());

        return builder.build();
    }

    @Override
    public JWTClaimsSet toClaimsSet(UserDetails userDetails) {
        // Prepare JWT with claims set
        JWTClaimsSet.Builder builder = getJwtClaimsSetBuilder(userDetails.getAuthorities());

        return builder.build();
    }

    private JWTClaimsSet.Builder getJwtClaimsSetBuilder(Collection<? extends GrantedAuthority> authorities) {
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

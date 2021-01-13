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
package top.dcenter.ums.security.jwt.supplier;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import top.dcenter.ums.security.jwt.api.supplier.JwtGrantedAuthoritiesConverterSupplier;
import top.dcenter.ums.security.jwt.claims.service.impl.UmsAuthoritiesClaimsSetServiceImpl;
import top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames;

import java.util.Collection;

/**
 * 简单的 JWT 转 Granted Authority 的转换器, Granted Authority 都放在 {@code authorities} 字段下, 没有 {@code scope}
 * 或 {@code scp} 字段. <br>
 * 注意: 替换此实现时请同时替换 {@link UmsAuthoritiesClaimsSetServiceImpl} 实现.
 * @see JwtGrantedAuthoritiesConverterSupplier
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.9 15:34
 */
public class UmsJwtGrantedAuthoritiesConverterSupplier implements JwtGrantedAuthoritiesConverterSupplier {
    @Override
    @NonNull
    public Converter<Jwt, Collection<GrantedAuthority>> getConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(JwtCustomClaimNames.AUTHORITIES.getClaimName());
        return jwtGrantedAuthoritiesConverter;
    }
}

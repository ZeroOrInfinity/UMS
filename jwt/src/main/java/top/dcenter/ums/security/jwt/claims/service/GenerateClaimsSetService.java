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
package top.dcenter.ums.security.jwt.claims.service;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * 根据 {@link Authentication} 生成 {@link JWTClaimsSet} 的接口
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.9 22:14
 */
public interface GenerateClaimsSetService {

    /**
     * 根据 {@link Authentication} 生成 {@link JWTClaimsSet},
     * 注意: {@link JWTClaimsSet} 中的"日期"都以"时间戳"表示且"时间戳"以秒为单位
     * @param authentication    authentication
     * @return  返回 {@link JWTClaimsSet}
     */
    @NonNull
    JWTClaimsSet generateClaimsSet(Authentication authentication);

    /**
     * 获取 JWT 存储 principal 的 claimName
     * @return 返回 JWT 存储 principal 的 claimName
     */
    @NonNull
    String getPrincipalClaimName();

    /**
     * 获取 {@link JwtAuthenticationConverter}
     * @return  返回 {@link JwtAuthenticationConverter}
     */
    @NonNull
    JwtAuthenticationConverter getJwtAuthenticationConverter();
}

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
package top.dcenter.ums.security.jwt.api.validator.service;

import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import top.dcenter.ums.security.jwt.supplier.UmsJwtClaimTypeConverterSupplier;

import java.util.Map;

/**
 * 对指定的 {@link #getClaimName()} 的对象进行校验的接口.<br>
 * 注意: {@link JwtClaimNames#EXP} / {@link JwtClaimNames#NBF} 和基于 {@link JwtClaimNames#JTI} 黑名单已实现, 不需要重复实现.
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.6 15:45
 */
public interface CustomClaimValidateService {

    /**
     * 对 {@link #getClaimName()} 进行有效性校验.
     * @param claimObject   {@link #getClaimName()} 的对象, 实际的对象可以通过 {@link UmsJwtClaimTypeConverterSupplier} 与
     *                      {@link MappedJwtClaimSetConverter#withDefaults(Map)} 查看.
     * @return  返回 true 时表示 {@link #getClaimName()} 校验通过.
     */
    boolean validate(Object claimObject);

    /**
     * 需要验证 Claim Name, 例如: scope, scp, {@link JwtClaimNames#AUD}, {@link JwtClaimNames#JTI} 等自定义的  Claim Name.
     * @return  返回需要验证的 Claim Name
     */
    String getClaimName();
}

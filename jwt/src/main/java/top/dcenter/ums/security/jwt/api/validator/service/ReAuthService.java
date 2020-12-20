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

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * 用于刷新 {@link Jwt} 有效期前校验是否需要用户重新登录的服务接口.
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.12 16:26
 */
public interface ReAuthService {

    /**
     * 刷新 {@link Jwt} 有效期前校验是否需要用户重新登录, 例如: 用户更改密码或用户信息更新或用户权限更新操作, {@link Jwt} 被加入黑名单等场景
     * @param jwt   需要刷新{@link Jwt}
     * @return  返回 true 表示需要重新登录, 返回 false 表示不需要重新登录.
     */
    Boolean isReAuth(Jwt jwt);
}

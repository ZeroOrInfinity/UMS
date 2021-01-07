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
package top.dcenter.ums.security.jwt.cache.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import top.dcenter.ums.security.jwt.api.cache.service.JwtCacheTransformService;

/**
 * {@link JwtCacheTransformService} 的默认实现
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.7 20:18
 */
public class UmsJwtCacheTransformServiceImpl implements JwtCacheTransformService<JwtAuthenticationToken> {

    @Override
    public JwtAuthenticationToken transform(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken)
        {
            return (JwtAuthenticationToken) authentication;
        }
        throw new RuntimeException("必须是 org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken 类型");
    }

    @Override
    public Class<JwtAuthenticationToken> getClazz() {
        return JwtAuthenticationToken.class;
    }
}

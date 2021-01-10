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
package demo.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkEndpointPermissionService;

import javax.servlet.http.HttpServletRequest;

/**
 * JwkSetUri(/.well-known/jwks.json) 访问权限
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.18 23:15
 */
@Component
public class JwkEndpointPermissionServiceImpl implements JwkEndpointPermissionService {

    @Override
    @NonNull
    public Boolean hasPermission(@NonNull HttpServletRequest request) {
        // ... 判断是否有有权限
        // 测试用例
        return Boolean.TRUE;
    }
}

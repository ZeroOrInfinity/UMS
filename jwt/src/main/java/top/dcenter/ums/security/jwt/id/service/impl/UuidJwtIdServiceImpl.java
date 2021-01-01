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
package top.dcenter.ums.security.jwt.id.service.impl;

import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import top.dcenter.ums.security.common.utils.UuidUtils;
import top.dcenter.ums.security.jwt.api.id.service.JwtIdService;

/**
 * {@link JwtClaimNames#JTI} id 生成服务, 替换此实现只需实现{@link JwtIdService} 并注入 IOC 容器即可.
 * @author YongWu zheng
 * @since 2021.1.1 10:31
 */
public class UuidJwtIdServiceImpl implements JwtIdService {

    @NonNull
    @Override
    public String generateJtiId() {
        return UuidUtils.getUUID();
    }

    @NonNull
    @Override
    public String generateRefreshToken() {
        return UuidUtils.getUUID();
    }
}

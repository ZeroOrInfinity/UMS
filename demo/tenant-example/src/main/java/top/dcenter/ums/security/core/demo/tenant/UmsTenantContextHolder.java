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
package top.dcenter.ums.security.core.demo.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.core.exception.TenantIdNotFoundException;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * 多租户上下文存储器, 多租户应用必须实现 {@link TenantContextHolder} 接口
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.1 13:31
 */
@Component
@Slf4j
public class UmsTenantContextHolder implements TenantContextHolder {

    private final ThreadLocal<String> context = new ThreadLocal<>();
    private final Auth2Properties auth2Properties;
    private final String systemTenantId = "0000";



    public UmsTenantContextHolder(Auth2Properties auth2Properties) {
        this.auth2Properties = auth2Properties;
    }

    @Override
    @NonNull
    public String tenantIdHandle(@NonNull HttpServletRequest request, @Nullable String tenantId) throws TenantIdNotFoundException {
        final String sessionId = request.getSession().getId();
        try {
            if (tenantId == null) {
                /* 从 request 中解析, 比如通过特定的请求头/cookie/requestURI解析;
                 * 示例中, 我们约定请求中的 uri 最后的路径值即为 tenantId, 比如 /user/signUp/111111 -> tenantId = 111111,
                 * /user/mobile/111111 -> tenantId = 111111
                 * 如果是第三方登录: /auth2/authorization/110110/gitee -> tenantId = 110110
                 */
                String uri = request.getServletPath();
                String auth2RedirectUrlPrefix = auth2Properties.getAuthLoginUrlPrefix();
                //noinspection AlibabaUndefineMagicConstant
                if (auth2RedirectUrlPrefix.endsWith("/*")) {
                    auth2RedirectUrlPrefix = auth2RedirectUrlPrefix.substring(0, auth2RedirectUrlPrefix.length() - 2);
                }

                if (uri.startsWith(auth2RedirectUrlPrefix)) {
                    // 第三方登录获取 tenantId
                    final int endIndex = uri.lastIndexOf("/");
                    uri = uri.substring(0, endIndex);
                }

                tenantId = uri.substring(uri.lastIndexOf("/") + 1);
            }
            // 存储 租户ID
            context.set(tenantId);
            return tenantId;
        }
        catch (Exception e) {
            log.error(ErrorCodeEnum.TENANT_ID_NOT_FOUND.getMsg(),e);
            return this.systemTenantId;
        }
    }

    @Override
    @NonNull
    public String getTenantId() throws TenantIdNotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            // 已登录用户获取租户 id
            return getTenantId(authentication);
        }

        try {
            String tenantId = this.context.get();
            if (tenantId == null) {
                throw new TenantIdNotFoundException(ErrorCodeEnum.TENANT_ID_NOT_FOUND, null, null);
            }
            return tenantId;
        }
        catch (Exception e) {
            throw new TenantIdNotFoundException(ErrorCodeEnum.TENANT_ID_NOT_FOUND, null, null);
        }
    }

    /**
     * 清除当前线程的值.
     */
    public void removeContext() {
        context.remove();
    }
}


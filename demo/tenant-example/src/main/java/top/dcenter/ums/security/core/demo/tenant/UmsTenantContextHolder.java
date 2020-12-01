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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.core.exception.TenantIdNotFoundException;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 多租户上下文存储器, 多租户应用必须实现 {@link TenantContextHolder} 接口
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.1 13:31
 */
@Component
public class UmsTenantContextHolder implements TenantContextHolder {

    /**
     * 租户 ID 缓存的时间, 单位秒
     */
    public static final int TIMEOUT = 30;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private Auth2Properties auth2Properties;

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
                String uri = request.getRequestURI();
                String auth2RedirectUrlPrefix = MvcUtil.getServletContextPath()  + auth2Properties.getAuthLoginUrlPrefix();
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
            // 示例直接用 redis 作为缓存 tenantId, 当然 tenantId 也可以存储在 session
            this.stringRedisTemplate.opsForValue().set(sessionId, tenantId, TIMEOUT, TimeUnit.SECONDS);
            return tenantId;
        }
        catch (Exception e) {
            throw new TenantIdNotFoundException(ErrorCodeEnum.TENANT_ID_NOT_FOUND, null, sessionId);
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

        // 未登录用户获取租户 id
        String sessionId = null;
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            sessionId = requestAttributes.getSessionId();
            String tenantId = this.stringRedisTemplate.opsForValue().get(sessionId);
            if (tenantId == null) {
                throw new TenantIdNotFoundException(ErrorCodeEnum.TENANT_ID_NOT_FOUND, null, sessionId);
            }
            return tenantId;
        }
        catch (Exception e) {
            throw new TenantIdNotFoundException(ErrorCodeEnum.TENANT_ID_NOT_FOUND, null, sessionId);
        }
    }
}


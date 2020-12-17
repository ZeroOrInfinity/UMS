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
package top.dcenter.ums.security.core.api.tenant.handler;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.TenantIdNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static top.dcenter.ums.security.common.consts.TenantConstants.DEFAULT_TENANT_PREFIX;


/**
 * 多租户上下文存储器. 实现此接口并注入 IOC 容器后, 会自动注入 UMS 默认实现的注册/登录/授权组件, 要实现 ums 框架具有多租户功能, 必须实现此接口并注入 IOC 容器.<br>
 * 功能: <br>
 * 1. {@link #tenantIdHandle(HttpServletRequest, String)}从注册用户入口或登录用户入口提取 tenantId 及进行必要的逻辑处理(如: tenantId 存入 ThreadLocal, 或存入 session, 或存入 redis 缓存等).<br>
 * 2. {@link #getTenantId()},方便后续用户注册、登录、授权的数据处理(如：sql 添加 tenantId 的条件，注册用户添加 TENANT_tenantId 权限，根据 tenantId 获取角色的权限数据).<br>
 * 3. {@link #getTenantId(Authentication)} 默认实现方法, 用户已登录的情况下, 获取租户 ID, 直接从 {@code authority} 中解析获取.<br>
 *
 * 注意:<br>
 * 1. 多租户系统中, 在未登录时需要用到 tenantId 的接口, 如: {@link UserCache#getUserFromCache(String)}/{@code UmsUserDetailsService} 等接口,
 * 可通过 {@link #getTenantId()} 来获取 tenantId. 登录用户可以通过 {@link Authentication} 来获取 tenantId.<br>
 *
 * 2. UMS 默认的登录与注册逻辑中, 都内置了 {@link #tenantIdHandle(HttpServletRequest, String)} 逻辑,
 * 用户在实现 {@link UserCache}/{@code UmsUserDetailsService} 等接口中需要 tenantId 时, 调用 {@link TenantContextHolder#getTenantId()} 方法即可.<br>
 *
 * 3. 如果自定义的注册或登录逻辑, 需要自己先调用 {@link #tenantIdHandle(HttpServletRequest, String)} 逻辑, 再在
 * 实现 {@link UserCache}/{@code UmsUserDetailsService} 等接口中需要 tenantId 时, 调用 {@link #getTenantId()} 方法即可.
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2020.11.30 14:05
 */
public interface TenantContextHolder {

    /**
     * 提取 tenantId 及进行必要的逻辑处理(如: tenantId 存入 ThreadLocal, 或存入 session, 或存入 redis 缓存等), 方便后续调用.
     * @param request   {@link HttpServletRequest}
     * @param tenantId  租户 id, 有时候用户自定义登录/注册等接口时, 可能 tenantId 通过 uri 模板变量传递, 此时通过此参数传递,
     *                  省的对 {@link HttpServletRequest} 进行解析, 如果需要对 {@link HttpServletRequest} 进行解析时, 此参数为 null.
     * @return  返回 tenantId
     * @throws TenantIdNotFoundException    获取不到租户 ID 异常
     */
    @NonNull
    String tenantIdHandle(@NonNull HttpServletRequest request, @Nullable String tenantId) throws TenantIdNotFoundException;

    /**
     * 获取租户 ID:
     * 1. 未登录情况从缓存获取租户 ID(如: 从 ThreadLocal 中获取 tenantId, 或从 session 中获取 tenantId, 或从 redis 缓存中获取 tenantId等).<br>
     * 2. 已登录情况可以调用默认方法 {@link #getTenantId(Authentication)}
     * <pre>
     * Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     * if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
     *     // 已登录用户获取租户 id
     *     return getTenantId(authentication);
     * }
     * </pre>
     * @return 返回 tenantId
     * @throws TenantIdNotFoundException    获取不到租户 ID 异常
     */
    @NonNull
    String getTenantId() throws TenantIdNotFoundException;

    /**
     * 用户已登录的情况下, 获取租户 ID, 直接从 {@code authority} 中解析获取.
     * @param authentication        {@link Authentication} 必须是登录成功的用户, 未登录用户直接抛 {@link TenantIdNotFoundException} 异常.
     * @return  返回租户 id
     * @throws TenantIdNotFoundException    获取不到租户 ID 异常
     */
    @NonNull
    default String getTenantId(@NonNull Authentication authentication) throws TenantIdNotFoundException {

        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            // 用户未登录的情况
            throw new TenantIdNotFoundException(ErrorCodeEnum.TENANT_ID_NOT_FOUND, null, authentication.getName());
        }

        // 用户已登录的情况
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String tenantId = null;
        String auth;
        for (GrantedAuthority authority : authorities) {
            auth = authority.getAuthority();
            if (auth.startsWith(DEFAULT_TENANT_PREFIX)) {
                tenantId = auth.substring(DEFAULT_TENANT_PREFIX.length());
                break;
            }
        }

        if (null == tenantId) {
            throw new TenantIdNotFoundException(ErrorCodeEnum.TENANT_ID_NOT_FOUND, null, authentication.getName());
        }

        return tenantId;
    }
}

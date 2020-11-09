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

package top.dcenter.ums.security.core.api.permission.service;

import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import top.dcenter.ums.security.core.permission.enums.PermissionType;
import top.dcenter.ums.security.core.permission.evaluator.UriAuthoritiesPermissionEvaluator;
import top.dcenter.ums.security.core.permission.service.DefaultUriAuthorizeService;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * uri(资源) 访问权限控制服务接口抽象类, 定义了基于(角色/多租户/SCOPE)的访问权限控制逻辑.<br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换
 * {@link DefaultUriAuthorizeService}.<br><br>
 * 注意:
 * 1. 推荐实现 {@link AbstractUriAuthorizeService} 同时实现 {@link UpdateAndCacheAuthoritiesService} 更新与缓存权限服务,
 * 有助于提高授权服务性能.<br>
 * 2. 对传入的 {@link Authentication} 的 authorities 硬性要求: <br>
 * <pre>
 * // 此 authorities 可以包含:  [ROLE_A, ROLE_B, ROLE_xxx TENANT_110110, SCOPE_read, SCOPE_write, SCOPE_xxx]
 * // 如上所示:
 * //    1. 角色数量    >= 1
 * //    2. SCOPE 数量 >= 0
 * //    3. 多租户数量  = 1 或 0
 * Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
 * </pre>
 * 3. 此框架默认实现 {@link #hasPermission(Authentication, HttpServletRequest)} 方法访问权限控制, 通过
 * {@link UriAuthoritiesPermissionEvaluator} 实现, 使用此接口的前提条件是: 应用使用的是 restful 风格的 API; <br>
 * 如果不是 restful 风格的 API, 请使用 {@link #hasPermission(Authentication, String, String)} 接口的访问权限控制, 此接口使用注解的方式 {@code @PerAuthorize("hasPermission('/users', 'list')")} 来实现,
 * 使用注解需开启 {@code @EnableGlobalMethodSecurity(prePostEnabled = true)} 注解.<br>
 *
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 15:09
 */
public abstract class AbstractUriAuthorizeService implements UriAuthorizeService {


    /**
     * 角色权限前缀
     */
    public static final String DEFAULT_ROLE_PREFIX = "ROLE_";
    /**
     * 多租户权限前缀
     */
    public static final String DEFAULT_TENANT_PREFIX = "TENANT_";
    /**
     * 资源权限前缀
     */
    public static final String DEFAULT_SCOPE_PREFIX = "SCOPE_";

    /**
     * 权限分隔符
     */
    public static final String PERMISSION_DELIMITER = ",";

    @Getter
    protected AntPathMatcher antPathMatcher = new AntPathMatcher();


    /**
     * 根据 authentication 来判断是否有 uri(资源) Authority 访问权限, <br>
     * 用于 {@code httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)")} 判断,
     * 使用此接口的前提条件是: restful 风格的 API.
     * @param authentication    authentication
     * @param request           HttpServletRequest
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    @Override
    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {

        String requestUri = MvcUtil.getUrlPathHelper().getPathWithinApplication(request);
        // Map<uri, Set<permission>>
        final Map<String, Set<String>> uriAuthorityOfUserRoleMap = getUriAuthorityMapOfUser(authentication);

        final String method = request.getMethod();

        return uriAuthorityOfUserRoleMap.entrySet()
                                        .stream()
                                        // 通过 antPathMatcher 检查用户权限集合中 uri 是否匹配 requestUri
                                        .filter(entry -> antPathMatcher.match(entry.getKey(), requestUri))
                                        // 用户的 authority 的 permission 是否 method 对应的 permission 后缀匹配
                                        .anyMatch(entry -> isMatchByMethod(method, entry.getValue()));

    }

    /**
     * 根据 authentication 来判断是否有 uri(资源) Authority 访问权限, 用于 {@code @PerAuthorize("hasPermission('/users', 'list')")} 判断
     * @param authentication    authentication
     * @param requestUri        不包含 ServletContextPath 的 requestUri
     * @param uriAuthority      uri 权限
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    @Override
    public boolean hasPermission(Authentication authentication, final String requestUri, final String uriAuthority) {

        // Map<uri, Set<permission>>
        Map<String, Set<String>> uriAuthorityOfUserRoleMap = getUriAuthorityMapOfUser(authentication);

        return uriAuthorityOfUserRoleMap.entrySet()
                                        .stream()
                                        // requestUri 是否匹配(antPathMatcher)用户所拥有权限的 uri
                                        .filter(entry -> antPathMatcher.match(entry.getKey(), requestUri))
                                        // uri 相对应的 authorities set 是否包含 userAuthority
                                        .anyMatch(entry -> entry.getValue().contains(uriAuthority));

    }

    /**
     * 获取用户角色的 uri 权限 Map
     * @param rolesAuthoritiesMap   所有角色 uri(资源) 权限 Map(role, map(uri, Set(permission)))
     * @param userRoleSet           用户所拥有的角色集合
     * @return 用户角色的 uri 权限 Map(uri, Set(permission))
     */
    @Override
    @NonNull
    public Map<String, Set<String>> getUriAuthoritiesOfUserRole(Map<String, Map<String, Set<String>>> rolesAuthoritiesMap,
                                                                final Set<String> userRoleSet) {

        // Map<uri, Set<permission>>
        Map<String, Set<String>> uriAuthoritiesMap = new HashMap<>(rolesAuthoritiesMap.size());

        rolesAuthoritiesMap.entrySet()
                           .stream()
                           .filter(entry -> userRoleSet.contains(entry.getKey()))
                           .map(Map.Entry::getValue)
                           .forEach(map2mapConsumer(uriAuthoritiesMap));

        return uriAuthoritiesMap;
    }

    /**
     * 根据 authentication 获取用户所拥有的所有角色的 uri(资源) 权限. 这里包含了多租户 与 SCOPE 逻辑. <br>
     * <pre>
     * Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
     * // 此 authorities 可以包含:  [ROLE_A, ROLE_B, TENANT_110110, SCOPE_read, SCOPE_write]
     * // 如上所示:
     * //    1. 角色数量    >= 1
     * //    2. SCOPE 数量 >= 1
     * //    3. 多租户数量  = 1
     * </pre>
     * @param authentication    {@link Authentication}
     * @return  用户所拥有的所有角色的 uri(资源) 权限 Map(uri, Set(permission))
     */
    @NonNull
    private Map<String, Set<String>> getUriAuthorityMapOfUser(@NonNull Authentication authentication) {

        // 获取角色权限集合
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        int size = authoritySet.size();
        // 存储用户角色的集合
        final Set<String> roleSet = new HashSet<>(size);
        // 存储多组户 ID
        final String[] tenantAuthority = new String[]{null};
        // 存储 SCOPE 的集合
        final Set<String> scopeSet = new HashSet<>(size);

        authoritySet.forEach(authority -> {
            if (authority.startsWith(DEFAULT_ROLE_PREFIX)) {
                roleSet.add(authority);
            }
            else if (authority.startsWith(DEFAULT_TENANT_PREFIX)) {
                tenantAuthority[0] = authority;
            }
            else if (authority.startsWith(DEFAULT_SCOPE_PREFIX)) {
                scopeSet.add(authority);
            }
        });

        // 用户所拥有的所有角色的 uri(资源) 权限 Map(uri, Set(permission))
        final Map<String, Map<String, Set<String>>> rolesAuthorities;

        if (null != tenantAuthority[0]) {
            // 获取此租户 ID 的所有角色的资源权限的 Map
            rolesAuthorities = getRolesAuthoritiesOfTenant(tenantAuthority[0]);
        }
        else if (scopeSet.size() > 0) {
            // 获取此 scopeSet 的所有角色的资源权限的 Map
            rolesAuthorities = getRolesAuthoritiesOfScope(scopeSet);
        }
        else {
            // 获取所有角色的资源权限的 Map
            rolesAuthorities = getRolesAuthorities();
        }

        // 基于用户 roleSet 的 Map<uri, Set<permission>>
        return getUriAuthoritiesOfUserRole(rolesAuthorities, roleSet);

    }

    @NonNull
    private Consumer<Map<String, Set<String>>> map2mapConsumer(final Map<String, Set<String>> uriAuthoritiesMap) {
        return map -> map.forEach(
                (key, value) -> uriAuthoritiesMap.compute(key, (k, v) ->
                        {
                            if (v == null)
                            {
                                v = new HashSet<>();
                            }
                            v.addAll(value);
                            return v;
                        }));
    }

    /**
     * 检查 userAuthoritySet 中的权限后缀是否与 requestMethod 相匹配.
     * @param requestMethod     requestMethod
     * @param userAuthoritySet  userAuthoritySet
     * @return  是否匹配
     */
    private boolean isMatchByMethod(@NonNull String requestMethod, @Nullable Set<String> userAuthoritySet) {
        if (CollectionUtils.isEmpty(userAuthoritySet))
        {
            return false;
        }

        String permission = PermissionType.getPermission(HttpMethod.valueOf(requestMethod));
        if (permission == null)
        {
            return false;
        }

        return userAuthoritySet.stream().anyMatch(authority -> authority.equals(permission));
    }

}
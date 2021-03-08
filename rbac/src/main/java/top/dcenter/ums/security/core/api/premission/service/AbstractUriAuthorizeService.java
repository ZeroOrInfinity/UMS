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

package top.dcenter.ums.security.core.api.premission.service;

import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import top.dcenter.ums.security.common.utils.UrlUtil;
import top.dcenter.ums.security.core.premission.enums.PermissionType;
import top.dcenter.ums.security.core.premission.evaluator.UriAuthoritiesPermissionEvaluator;
import top.dcenter.ums.security.core.premission.service.DefaultUriAuthorizeService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static top.dcenter.ums.security.common.consts.RbacConstants.DEFAULT_GROUP_PREFIX;
import static top.dcenter.ums.security.common.consts.RbacConstants.DEFAULT_ROLE_PREFIX;
import static top.dcenter.ums.security.common.consts.RbacConstants.DEFAULT_SCOPE_PREFIX;
import static top.dcenter.ums.security.common.consts.RbacConstants.PERMISSION_SEPARATOR;
import static top.dcenter.ums.security.common.consts.TenantConstants.DEFAULT_TENANT_PREFIX;

/**
 * uri(资源) 访问权限控制服务接口抽象类, 定义了基于(角色/多租户/SCOPE)的访问权限控制逻辑.<br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换
 * {@link DefaultUriAuthorizeService}.<br><br>
 * 注意:
 * 1. 推荐实现 {@link AbstractUriAuthorizeService} 同时实现 {@link UpdateCacheOfRolesResourcesService} 更新与缓存权限服务,
 * 有助于提高授权服务性能.<br>
 * 2. 对传入的 {@link Authentication} 的 authorities 硬性要求: <br>
 * <pre>
 * // 此 authorities 可以包含:  [ROLE_A, ROLE_B, ROLE_xxx TENANT_110110, SCOPE_read, SCOPE_write, SCOPE_xxx]
 * // 如上所示:
 * //    1. 角色数量    >= 0
 * //    2. SCOPE 数量 >= 0
 * //    3. 多租户数量  1 或 0
 * //    4. 角色数量 + SCOPE 数量  >= 1
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

    @Getter
    protected AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 用于基于 角色 的权限控制的更新或缓存所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口.<br>
     * 注意: 要考虑并发更新问题.
     */
    protected abstract void updateAuthoritiesOfAllRoles();
    /**
     * 用于基于 多租户 的权限控制的更新或缓存所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口.<br>
     * 注意: 要考虑并发更新问题.
     */
    protected abstract void updateAuthoritiesOfAllTenant();
    /**
     * 用于基于 SCOPE 的权限控制的更新或缓存所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口.<br>
     * 注意: 要考虑并发更新问题.
     */
    protected abstract void updateAuthoritiesOfAllScopes();
    /**
     * 用于 角色组(Group) 的组角色的更新或缓存所有角色的权限服务, 每次更新组角色时,需要调用此接口.<br>
     * 注意: 要考虑并发更新问题.
     */
    protected abstract void updateAllGroupsOfAllTenant();

    /**
     * 根据 authentication 来判断是否有 request 所代表的 资源 的访问权限, <br>
     * 用于 {@code httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)")} 判断,
     * 使用此接口的前提条件是: restful 风格的 API.
     * @param authentication    authentication
     * @param request           HttpServletRequest
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    @Override
    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {

        final String requestUri = UrlUtil.getUrlPathHelper().getPathWithinApplication(request);
        final String method = request.getMethod();

        // Map.Entry<uri, Set<permission>>, 根据 method 获取对应权限是否包含在 entry 中的 Set<permission> 中
        final Predicate<Map.Entry<String, Set<String>>> predicate = entry -> isMatchByMethod(method, entry.getValue());

        return hasPermission(authentication, requestUri, predicate);

    }

    /**
     * 根据 authentication 来判断是否有 requestUri(资源) 的访问权限(permission), 用于 {@code @PerAuthorize("hasPermission('/users', 'list')")} 判断
     * @param authentication    authentication
     * @param requestUri        不包含 ServletContextPath 的 requestUri
     * @param permission        uri 权限
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    @Override
    public boolean hasPermission(Authentication authentication, final String requestUri, final String permission) {

        // Map.Entry<uri, Set<permission>>,  Set<permission> 中是否包含此 permission
        final Predicate<Map.Entry<String, Set<String>>> predicate = entry -> entry.getValue().contains(permission);

        return hasPermission(authentication, requestUri, predicate);

    }

    private boolean hasPermission(Authentication authentication, String requestUri, Predicate<Map.Entry<String, Set<String>>> predicate) {
        // Map<uri, Set<permission>>
        final Map<String, Set<String>> uriPermissionsOfUser = getUriAuthoritiesOfUser(authentication);
        return uriPermissionsOfUser.entrySet()
                                   .stream()
                                   // 通过 antPathMatcher 检查用户权限Map中 uri 是否匹配 requestUri
                                   .filter(entry -> antPathMatcher.match(entry.getKey(), requestUri))
                                   // 是否匹配 predicate
                                   .anyMatch(predicate);
    }

    /**
     * 根据 authentication 获取用户所拥有的角色与 scope 的 uri(资源) 权限. 这里包含了多租户 与 SCOPE 逻辑. <br>
     * <pre>
     * Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
     * // 此 authorities 可以包含:  [ROLE_A, ROLE_B, TENANT_110110, SCOPE_read, SCOPE_write]
     * // authorities 要求:
     * //    1. 角色数量    >= 0
     * //    2. SCOPE 数量 >= 0
     * //    3. 多租户数量  1 或 0
     * //    4. 角色数量 + SCOPE 数量  >= 1
     * </pre>
     * @param authentication    {@link Authentication}
     * @return  用户所拥有的角色与 scope 的 uri(资源) 权限 Map(uri, Set(permission))
     */
    @Override
    @NonNull
    public Map<String, Set<String>> getUriAuthoritiesOfUser(@NonNull Authentication authentication) {

        // 获取角色权限集合
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        int size = authoritySet.size();
        // 存储用户角色的集合
        final Set<String> roleSet = new HashSet<>(size, 1.F);
        // 存储多组户 ID
        final String[] tenantAuthority = new String[]{null};
        // 存储 SCOPE 的集合
        final Set<String> scopeAuthoritySet = new HashSet<>(size, 1.F);

        groupByRoleOrTenantOrScope(authoritySet, roleSet, tenantAuthority, scopeAuthoritySet);

        // 用户所拥有的所有角色的 uri(资源) 权限 Map(uri, Set(permission))
        final Map<String, Map<String, Set<String>>> rolesAuthorities;

        if (null != tenantAuthority[0]) {
            // 获取此租户 ID 的所有角色的资源权限的 Map
            rolesAuthorities = getRolesAuthoritiesOfTenant(tenantAuthority[0]);
        }
        else if (roleSet.size() > 0) {
            // 获取所有角色的资源权限的 Map
            rolesAuthorities = getRolesAuthorities();
        }
        else {
            rolesAuthorities = new HashMap<>(0);
        }

        // 基于用户 roleSet 的 Map<uri, Set<permission>>
        final Map<String, Set<String>> uriPermissionsOfUserRole = getUriAuthoritiesOfUserRole(rolesAuthorities, roleSet);

        if (scopeAuthoritySet.size() > 0) {
            // 获取此 scopeAuthoritySet 的所有角色的资源权限的 Map<role, Map<uri, Set<permission>>>
            final Map<String, Map<String, Set<String>>> uriPermissionsOfScope = getScopeAuthoritiesOfScope(scopeAuthoritySet);
            // 把 scope 的资源权限与 role 资源权限合并
            if (!uriPermissionsOfScope.isEmpty()) {
                uriPermissionsOfScope.values().forEach(map2mapConsumer(uriPermissionsOfUserRole));
            }
        }
        return uriPermissionsOfUserRole;

    }

    private void groupByRoleOrTenantOrScope(@NonNull Set<String> authoritySet,
                                            @NonNull Set<String> toRoleSet,
                                            @NonNull String[] toTenantAuthority,
                                            @NonNull Set<String> toScopeAuthoritySet) {
        List<String> groupList = null;
        // 对 authoritySet 根据 role/tenant/scope 进行分组
        for (String authority : authoritySet) {
            int indexOf = authority.indexOf(PERMISSION_SEPARATOR);
            if (indexOf != -1) {
                switch (authority.substring(0, indexOf + 1)) {
                    case DEFAULT_ROLE_PREFIX:
                        toRoleSet.add(authority);
                        break;
                    case DEFAULT_TENANT_PREFIX:
                        toTenantAuthority[0] = authority;
                        break;
                    case DEFAULT_SCOPE_PREFIX:
                        toScopeAuthoritySet.add(authority);
                        break;
                    case DEFAULT_GROUP_PREFIX:
                        if (isNull(groupList)) {
                        	groupList = new ArrayList<>();
                        }
                        groupList.add(authority);
                        break;
                    default:
                }
            }
        }
        if (null != groupList && groupList.size() > 0) {
            String tenantAuthority = toTenantAuthority[0];
            Set<String> groupRoleSet;
            for (String groupAuthority : groupList) {
                if (isNull(tenantAuthority)) {
                    groupRoleSet = this.getRolesByGroup(groupAuthority);
                }
                else {
                    groupRoleSet = this.getRolesByGroupOfTenant(tenantAuthority, groupAuthority);
                }
                toRoleSet.addAll(groupRoleSet);
            }
        }
    }

    /**
     * 获取用户角色的 uri 权限 Map
     * @param rolesAuthoritiesMap   所有角色 uri(资源) 权限 Map(role, map(uri, Set(permission)))
     * @param userRoleSet           用户所拥有的角色集合
     * @return 用户角色的 uri 权限 Map(uri, Set(permission))
     */
    @NonNull
    private Map<String, Set<String>> getUriAuthoritiesOfUserRole(@NonNull Map<String, Map<String, Set<String>>> rolesAuthoritiesMap,
                                                                 @NonNull final Set<String> userRoleSet) {

        // Map<uri, Set<permission>>
        Map<String, Set<String>> uriAuthoritiesMap = new HashMap<>(rolesAuthoritiesMap.size());

        rolesAuthoritiesMap.entrySet()
                           .stream()
                           .filter(entry -> userRoleSet.contains(entry.getKey()))
                           .map(Map.Entry::getValue)
                           .forEach(map2mapConsumer(uriAuthoritiesMap));

        return uriAuthoritiesMap;
    }

    @NonNull
    private Consumer<Map<String, Set<String>>> map2mapConsumer(@NonNull final Map<String, Set<String>> uriAuthoritiesMap) {
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
     * 检查 userPermissionSet 中的权限后缀是否与 requestMethod 相匹配.
     * @param requestMethod      requestMethod
     * @param userPermissionSet  userPermissionSet
     * @return  是否匹配
     */
    private boolean isMatchByMethod(@NonNull String requestMethod, @Nullable Set<String> userPermissionSet) {
        if (CollectionUtils.isEmpty(userPermissionSet))
        {
            return false;
        }

        String permission = PermissionType.getPermission(HttpMethod.valueOf(requestMethod));
        if (permission == null)
        {
            return false;
        }

        return userPermissionSet.stream().anyMatch(authority -> authority.equals(permission));
    }

}
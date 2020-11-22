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

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;
import top.dcenter.ums.security.core.permission.enums.PermissionType;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * uri(资源) 访问权限控制服务接口 <br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/8/28 16:09
 */
public interface UriAuthorizeService {

    /**
     * 根据 authentication 来判断是否有 requestUri(资源) 的访问权限(permission), 用于 {@code @PerAuthorize("hasPermission('/users', 'list')")} 判断
     * @param authentication    authentication
     * @param requestUri        不包含 ServletContextPath 的 requestUri
     * @param permission      uri 权限
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    boolean hasPermission(Authentication authentication, String requestUri, String permission);

    /**
     * 根据 authentication 来判断是否有 request 所代表的 资源 的访问权限, <br>
     * 用于 {@code httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)")} 判断,
     * 使用此接口的前提条件是: restful 风格的 API.
     * @param authentication    authentication
     * @param request           HttpServletRequest
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    boolean hasPermission(Authentication authentication, HttpServletRequest request);

    /**
     * 根据 authentication 获取用户所拥有的角色与 scope 的 uri(资源) 权限. 这里包含了多租户 与 SCOPE 逻辑. <br>
     * <pre>
     * Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
     * // 此 authorities 可以包含:  [ROLE_A, ROLE_B, TENANT_110110, SCOPE_read, SCOPE_write]
     * // 如上所示:
     * //    1. 角色数量    >= 1
     * //    2. SCOPE 数量 >= 1
     * //    3. 多租户数量  = 1
     * </pre>
     * @param authentication    {@link Authentication}
     * @return  用户所拥有的角色与 scope 的 uri(资源) 权限 Map(uri, Set(permission))
     */
    @NonNull
    Map<String, Set<String>> getUriAuthoritiesOfUser(@NonNull Authentication authentication);

    /**
     * 获取所有角色的 uri(资源) 的权限 Map(role, Map(uri, Set(permission))).<br>
     * <pre>
     * // 当为 restful 风格的 Api 时, uri 与 permission 是一对一关系:
     *  uri         permission
     *  /user/*     list
     *  /user/*     add
     *  /user/*     edit
     *  /user/*     delete
     *
     * // 当不是 restful 风格的 Api 时, uri 与 permission 可能是一对一关系, 也可能是一对多关系:
     *  uri         permission
     *  /user/*     list
     *  /user/*     add,edit
     *  /user/*     delete
     *
     * // 但最终返回的结果时是一样的; Map{["user/*", Set[list,add,edit,delete]]..}
     * </pre>
     * @return Map(role, Map(uri, Set(permission))): <br>
     *     key: 必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), <br>
     *     value: map(key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为权限字符串({@link PermissionType#getPermission()}) Set).
     */
    @NonNull
    default Map<String, Map<String, Set<String>>> getRolesAuthorities(){
        // 默认为空, 使用者需自己实现此逻辑
        throw new RuntimeException("未实现基于 角色 的权限控制的更新或缓存所有角色的权限服务");
    }

    /**
     * 获取 指定租户 所有角色的 uri(资源) 的权限 Map(role, Map(uri, Set(permission))).<br>
     * <pre>
     * // 当为 restful 风格的 Api 时, uri 与 permission 是一对一关系:
     *  uri         permission
     *  /user/*     list
     *  /user/*     add
     *  /user/*     edit
     *  /user/*     delete
     *
     * // 当不是 restful 风格的 Api 时, uri 与 permission 可能是一对一关系, 也可能是一对多关系:
     *  uri         permission
     *  /user/*     list
     *  /user/*     add,edit
     *  /user/*     delete
     *
     * // 但最终返回的结果时是一样的; Map{["user/*", Set[list,add,edit,delete]]..}
     * </pre>
     * @param tenantAuthority   包含 TENANT_ 前缀的租户权限, 例如: TENANT_租户ID
     * @return                  Map(role, Map(uri, Set(permission))): <br>
     *     key: 必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), <br>
     *     value: map(key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为权限字符串({@link PermissionType#getPermission()}) Set).
     */
    @NonNull
    default Map<String, Map<String, Set<String>>> getRolesAuthoritiesOfTenant(String tenantAuthority) {
        // 默认为空, 多租户使用者需自己实现此逻辑
        return Collections.emptyMap();
    }

    /**
     * 获取指定 scopeSet 的所有SCOPE的 uri(资源) 的权限 Map(scope, Map(uri, Set(permission))). 在微服务中, 对资源的权限控制有粗粒度权限控制
     * 与细粒度权限控制, 使用粗粒度权限控制时: 一般通过网关或授权服务器进行权限控制, 此接口适用此模式.<br>
     * <pre>
     * // 当为 restful 风格的 Api 时, uri 与 permission 是一对一关系:
     *  uri         permission
     *  /user/*     list
     *  /user/*     add
     *  /user/*     edit
     *  /user/*     delete
     *
     * // 当不是 restful 风格的 Api 时, uri 与 permission 可能是一对一关系, 也可能是一对多关系:
     *  uri         permission
     *  /user/*     list
     *  /user/*     add,edit
     *  /user/*     delete
     *
     * // 但最终返回的结果时是一样的; Map{["user/*", Set[list,add,edit,delete]]..}
     * </pre>
     * @param scopeSet          包含 SCOPE_ 前缀的租户权限 Set, 例如: SCOPE_scope
     * @return                  Map(scope, Map(uri, Set(permission))): <br>
     *     key: 必须包含"SCOPE_"前缀的角色名称(如: SCOPE_read), <br>
     *     value: map(key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为权限字符串({@link PermissionType#getPermission()}) Set).
     */
    @NonNull
    default Map<String, Map<String, Set<String>>> getScopeAuthoritiesOfScope(Set<String> scopeSet) {
        // 默认为空, SCOPE 使用者需自己实现此逻辑
        return Collections.emptyMap();
    }

    /**
     * 获取 AntPathMatcher
     * @return AntPathMatcher
     */
    AntPathMatcher getAntPathMatcher();

}
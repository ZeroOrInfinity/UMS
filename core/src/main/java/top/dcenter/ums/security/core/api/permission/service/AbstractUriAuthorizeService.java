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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import top.dcenter.ums.security.core.api.permission.entity.UriResourcesDTO;
import top.dcenter.ums.security.core.permission.enums.PermissionType;
import top.dcenter.ums.security.core.permission.service.DefaultUriAuthorizeService;
import top.dcenter.ums.security.core.util.ConvertUtil;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * request 的 uri 访问权限控制服务.<br><br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换
 * {@link DefaultUriAuthorizeService}.<br><br>
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 15:09
 */
public abstract class AbstractUriAuthorizeService implements UriAuthorizeService, InitializingBean {


    /**
     * 角色权限前缀
     */
    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";
    /**
     * 多租户权限前缀
     */
    private static final String DEFAULT_TENANT_PREFIX = "TENANT_";
    /**
     * 资源权限前缀
     */
    private static final String DEFAULT_SCOPE_PREFIX = "SCOPE_";

    /**
     * 权限分隔符
     */
    public static final String PERMISSION_DELIMITER = ",";

    /**
     * 所有角色 uri 权限 Map(role, map(uri, uriResourcesDTO))
     */
    protected volatile Map<String, Map<String, UriResourcesDTO>> rolesAuthoritiesMap;
    /**
     * 所有角色 uri 权限 Map(uri, Set(authority))
     */
    private volatile Map<String, Set<String>> uriAuthoritiesMap;

    @Getter
    protected AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final Object lock = new Object();


    @Override
    public boolean match(String pattern, String requestUri) {
        return antPathMatcher.match(pattern, requestUri);
    }

    /**
     * 根据 authentication 来判断是否有 uriAuthority 访问权限, <br>
     * 用于 {@code httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)")} 判断
     * @param authentication    authentication
     * @param request           HttpServletRequest
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    @Override
    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {

        String requestUri = MvcUtil.getUrlPathHelper().getPathWithinApplication(request);

        // 基于用户 role 的 Map(uri, Set(authority))
        final Map<String, Set<String>> uriAuthorityOfUserRoleMap =
                getUriAuthoritiesOfUserRole(authentication).orElse(new HashMap<>(0));

        final String method = request.getMethod();

        return uriAuthorityOfUserRoleMap.entrySet()
                                        .stream()
                                        // 通过 antPathMatcher 检查用户权限集合中 uri 是否匹配 requestUri
                                        .filter(entry -> antPathMatcher.match(entry.getKey(), requestUri))
                                        // 用户的 authority 的 permission 是否 method 对应的 permission 后缀匹配
                                        .anyMatch(entry -> isMatchByMethod(method, entry.getValue()));

    }

    /**
     * 根据 authentication 来判断是否有 uriAuthority 访问权限, 用于 {@code @PerAuthorize("hasPermission('/users', 'list')")} 判断
     * @param authentication    authentication
     * @param requestUri        不包含 ServletContextPath 的 requestUri
     * @param uriAuthority      uri 权限
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    @Override
    public boolean hasPermission(Authentication authentication, final String requestUri, final String uriAuthority) {

        // Map(uri, Set(authority))
        Map<String, Set<String>> uriAuthorityOfUserRoleMap = getUriAuthoritiesOfUserRole(authentication).orElse(new HashMap<>(0));

        // requestUri 是否匹配(antPathMatcher)用户所拥有权限的 uri 且与 uri 相对应的 authorities set 包含 userAuthority
        return uriAuthorityOfUserRoleMap.entrySet()
                                        .stream()
                                        .filter(entry -> antPathMatcher.match(entry.getKey(), requestUri))
                                        .anyMatch(entry -> entry.getValue().contains(uriAuthority));

    }

    /**
     * 根据 authentication 中 Authorities 的 roles 从 {@link #getRolesAuthorities()} 获取 uri 权限 map.<br><br>
     * 实现对 uri 的权限控制时, 要考虑纯正的 restful 风格的 api 是通过 GET/POST/PUT/DELETE 等来区别 curd 操作的情况;
     * 这里用 map(uri, Set(authority)) 来处理.
     * @param authentication    authentication
     * @return 用户角色的 uri 权限 Map(uri, Set(authority))
     */
    @Override
    public Optional<Map<String, Set<String>>> getUriAuthoritiesOfUserRole(Authentication authentication) {

        if (authentication == null)
        {
            return Optional.of(Collections.emptyMap());
        }
        // 获取角色权限集合
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        final Set<String> roleSet =
                authoritySet.stream()
                            .filter(authority -> authority.startsWith(DEFAULT_ROLE_PREFIX))
                            .collect(Collectors.toSet());

        /*
         * 实现对 requestUri 的权限控制时, 要考虑纯正的 restful 风格的 api 是通过 GET/POST/PUT/DELETE 等来区别 curd 操作的情况;
         * 这里我们用 map(uri, Set(authority)) 来处理
         */
        Map<String, Set<String>> uriAuthoritiesMap = new HashMap<>(rolesAuthoritiesMap.size());

        rolesAuthoritiesMap.entrySet()
                           .stream()
                           .filter(entry -> roleSet.contains(entry.getKey()))
                           .map(Map.Entry::getValue)
                           .forEach(map2mapConsumer(uriAuthoritiesMap));

        return Optional.of(uriAuthoritiesMap);
    }

    /**
     * 从 {@link #getRolesAuthorities()} 中获取获取所有 roles 的 uri 权限 map(uri, Set(authority)).<br><br>
     * @return 所有角色 uri 权限 Map(uri, Set(authority))
     */
    @Override
    public Optional<Map<String, Set<String>>> getUriAuthoritiesOfAllRole() {

        /*
         * 从 #getRolesAuthorities() 中获取获取所有 roles 的 uri 权限 map(uri, Set(authority))
         */
        return Optional.of(this.uriAuthoritiesMap);
    }


    @Override
    public void updateRolesAuthorities() {

        synchronized (lock) {
            Map<String, Map<String, UriResourcesDTO>> rolesAuthoritiesMap = getRolesAuthorities().orElse(new HashMap<>(0));
            Map<String, Set<String>> uriAuthoritiesMap = new HashMap<>(rolesAuthoritiesMap.size());
            rolesAuthoritiesMap.values().forEach(map2mapConsumer(uriAuthoritiesMap));
            this.uriAuthoritiesMap = uriAuthoritiesMap;
            this.rolesAuthoritiesMap = rolesAuthoritiesMap;
        }

    }

    @Override
    public void afterPropertiesSet() {
        // 角色 uri 权限 Map(role, map(uri, uriResourcesDTO))
        updateRolesAuthorities();

    }

    @NonNull
    private Consumer<Map<String, UriResourcesDTO>> map2mapConsumer(final Map<String, Set<String>> uriAuthoritiesMap) {
        return map -> map.forEach(
                (key, value) -> uriAuthoritiesMap.compute(key, (k, v) ->
                        {
                            if (v == null)
                            {
                                v = new HashSet<>();
                            }
                            v.addAll(ConvertUtil.string2Set(value.getPermission(), PERMISSION_DELIMITER));
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

        return userAuthoritySet.stream()
                .anyMatch(authority -> authority.equals(permission));
    }

}
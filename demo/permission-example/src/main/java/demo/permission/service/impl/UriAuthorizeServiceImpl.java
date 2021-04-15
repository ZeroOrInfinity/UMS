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

package demo.permission.service.impl;

import demo.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.api.permission.service.UpdateCacheOfRolesResourcesService;
import top.dcenter.ums.security.core.exception.RolePermissionsException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * request 的 uri 访问权限控制服务.<br>
 * 注意: 角色的 uri(资源) 权限更新与缓存<br>
 * 1. 基于 角色 的权限控制: 简单的实现 {@link #initAllAuthorities()} 的接口, 实现所有角色 uri(资源) 的权限
 * Map(roleAuthority, map(uri, Set(permission))) 的更新与缓存本机内存.
 * 2. 基于 SCOPE 的权限控制: 情况复杂一点, 但 SCOPE 类型比较少, 也还可以像 1 的方式实现缓存本机内存与更新.
 * 3. 基于 多租户 的权限控制: 情况比较复杂, 租户很少的情况下, 也还可以全部缓存在本机内存, 通常情况下全部缓存本机内存不现实, 只能借助于类似 redis 等的内存缓存.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Service
@Slf4j
public class UriAuthorizeServiceImpl extends AbstractUriAuthorizeService implements UpdateCacheOfRolesResourcesService, InitializingBean {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 所有角色 uri(资源) 权限 Map(roleAuthority, map(uri, Set(permission)))
     */
    private volatile Map<String, Map<String, Set<String>>> rolesAuthoritiesMap;

    private final Object lock = new Object();

    @Override
    public boolean updateAuthoritiesByRoleId(@NonNull Long roleId,
                                             @NonNull Class<?> resourceClass,
                                             Long... resourceIds) throws RolePermissionsException {
        // 实时更新指定角色的权限
        // Map<String, Map<String, Set<String>>> roleAuthoritiesMap =
        //     sysRoleService.getAuthoritiesByRoleIdAndResourceIds(roleId, resourceIds);
        // this.rolesAuthoritiesMap.put(roleAuthority, roleAuthoritiesMap.get(roleId));
        return true;
    }

    @Override
    public void initAllAuthorities() {

        synchronized (lock) {
            if (this.rolesAuthoritiesMap != null) {
                return;
            }
            // 更新并缓存所有角色 uri(资源) 权限 Map<roleAuthority, Map<uri, Set<permission>>>
            this.rolesAuthoritiesMap = new ConcurrentHashMap<>(updateRolesAuthorities());
        }

    }

    @Override
    public void afterPropertiesSet() {
        // 初始化所有角色 uri(资源) 权限 Map<roleAuthority, Map<uri, Set<permission>>>
        initAllAuthorities();

    }

    /**
     * 更新并缓存所有角色 uri(资源) 权限 Map<roleAuthority, Map<uri, Set<permission>>>.<br>
     * @return 所有角色 uri(资源) 权限 Map<roleAuthority, Map<uri, Set<permission>>>
     */
    @NonNull
    private Map<String, Map<String, Set<String>>> updateRolesAuthorities() {

        // 从数据源获取所有角色的权限
        final Map<String, Map<String, Set<String>>> rolesAuthoritiesMap = sysRoleService.getRolesAuthorities();


        this.rolesAuthoritiesMap = rolesAuthoritiesMap;

        return rolesAuthoritiesMap;
    }

    /**
     * 获取角色的 uri 的权限 map.<br>
     *     返回值为: Map(role, Map(uri, UriResourcesDTO))
     * @return Map(String, Map(String, String)) 的 key 为必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), value 为 UriResourcesDTO map
     * (key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为 UriResourcesDTO).
     */
    @Override
    @NonNull
    public Map<String, Map<String, Set<String>>> getRolesAuthorities() {

        if (this.rolesAuthoritiesMap != null) {
            return this.rolesAuthoritiesMap;
        }
        synchronized (lock) {
            if (this.rolesAuthoritiesMap != null) {
                return this.rolesAuthoritiesMap;
            }
            // 更新并缓存所有角色 uri(资源) 权限 Map<roleAuthority, Map<uri, Set<permission>>>
            return new ConcurrentHashMap<>(updateRolesAuthorities());
        }

    }

}
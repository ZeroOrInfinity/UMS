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

import demo.entity.SysResources;
import demo.entity.SysRole;
import demo.entity.SysRoleResources;
import demo.service.SysResourcesService;
import demo.service.SysRoleResourcesService;
import demo.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.dcenter.ums.security.core.api.permission.service.RolePermissionsService;
import top.dcenter.ums.security.core.api.permission.service.UpdateCacheOfRolesResourcesService;
import top.dcenter.ums.security.core.exception.RolePermissionsException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * uri 权限服务.<br> 此实现只是为了测试方便, 实际项目中建议实现 {@link RolePermissionsService} 接口来实现角色的资源权限的增删改查<br>
 * 注意：<br>
 * 如果实现了 {@link UpdateCacheOfRolesResourcesService} 接口, 未实现 {@link RolePermissionsService} 接口, 修改或添加基于"角色/多租户/SCOPE
 * "的资源权限时一定要调用 {@link UpdateCacheOfRolesResourcesService} 对应的方法, 有两种方式: 一种发布事件, 另一种是直接调用对应服务;<br>
 * <pre>
 *     // 1. 推荐用发布事件(异步执行)
 *     applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, UpdateRoleResourcesDto);
 *     // 2. 直接调用服务
 *     // 角色权限资源
 *     UpdateCacheOfRolesResourcesService.updateAuthoritiesByRoleId(roleId, resourceClass, resourceIds);
 *     // 多租户的角色权限资源
 *     UpdateCacheOfRolesResourcesService.updateAuthoritiesByRoleIdOfTenant(tenantId, roleId, resourceClass, resourceIds);
 *     // SCOPE 的角色权限资源
 *     UpdateCacheOfRolesResourcesService.updateAuthoritiesByScopeId(scopeId, roleId, resourceClass, resourceIds);
 *     // 角色组权限资源
 *     UpdateCacheOfRolesResourcesService.updateRolesByGroupId(groupId, roleIds);
 *     // 多租户的角色组权限资源
 *     UpdateCacheOfRolesResourcesService.updateRolesByGroupIdOfTenant(tenantId, groupId, roleIds);
 * </pre>
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020-09-26 22:41
 */
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
public class RolePermissionServiceImpl implements RolePermissionsService<SysResources> {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysResourcesService sysResourcesService;
    @Autowired
    private SysRoleResourcesService sysRoleResourcesService;

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Override
    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    public boolean updateResourcesByRoleId(@NonNull Long roleId, Long... resourceIds) throws RolePermissionsException {

        // 1. 删除已有的角色资源
        List<SysRoleResources> roleResources = sysRoleResourcesService.findByRoleId(roleId);
        if (roleResources.size() > 0) {
            sysRoleResourcesService.deleteAll(roleResources);
        }

        // 2. 添加新的角色资源
        List<SysRoleResources> roleResourcesList = new ArrayList<>();
        for (Long resourceId : resourceIds) {
            SysRoleResources sysRoleResources = new SysRoleResources();
            sysRoleResources.setRoleId(roleId);
            sysRoleResources.setResourcesId(resourceId);
            roleResourcesList.add(sysRoleResources);
        }
        sysRoleResourcesService.saveAll(roleResourcesList);

        return true;
    }

    @NonNull
    @Override
    public List<SysResources> findAllResourcesByRoleId(@NonNull Long roleId) {
        // 1. 获取角色
        Optional<SysRole> sysRoleOptional = sysRoleService.findById(roleId);
        // 角色不存在
        if (!sysRoleOptional.isPresent()) {
            return new ArrayList<>();
        }
        SysRole sysRole = sysRoleOptional.get();

        // start: 判断是否有权限获取此角色
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new ArrayList<>();
        }
        // 根据角色的 authorities 获取所有的继承角色, 包含本角色
        final Collection<? extends GrantedAuthority> authorities =
                roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());
        // 是否包含在权限继承链中.
        final boolean isInclude =
                authorities.stream().anyMatch(authority -> authority.getAuthority().equals(sysRole.getName()));
        if (!isInclude) {
            return new ArrayList<>();
        }
        // end: 判断是否有权限获取此角色

        // 2. 获取角色 uri 的对应权限资源,
        return sysResourcesService.findByRoleId(sysRole.getId());
    }

    @NonNull
    @Override
    public Class<SysResources> getUpdateResourcesClass() {
        return SysResources.class;
    }

}
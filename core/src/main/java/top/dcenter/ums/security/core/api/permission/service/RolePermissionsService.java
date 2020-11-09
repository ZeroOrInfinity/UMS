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

import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import top.dcenter.ums.security.core.exception.RolePermissionsException;
import top.dcenter.ums.security.core.permission.enums.PermissionType;

import java.util.List;

/**
 * 更新与查询基于(角色/多租户/SCOPE)的角色资源服务接口. 主要用于给角色添加权限的操作.<br>
 * 注意: <br>
 * 1. 在添加资源时, 通过{@link PermissionType#getPermission()} 来规范的权限格式, 因为要支持 restful 风格的 Api,
 * 在授权时需要对 {@link HttpMethod} 与对应的权限进行匹配判断<br>
 * 2. 如果实现了 {@link UpdateAndCacheAuthoritiesService} 接口, 未实现 {@link RolePermissionsService} 接口, 修改或添加基于"角色/多租户/SCOPE
 * "的资源权限时一定要调用 {@link UpdateAndCacheAuthoritiesService} 对应的方法, 有两种方式: 一种发布事件, 另一种是直接调用对应服务;<br>
 * <pre>
 *     // 1. 推荐用发布事件(异步执行)
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true, ResourcesType.ROLE));
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true, ResourcesType.TENANT));
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true, ResourcesType.SCOPE));
 *     // 2. 直接调用服务
 *     // 基于角色
 *     UpdateAndCacheAuthoritiesService.updateAuthoritiesOfAllRoles();
 *     // 基于多租户
 *     UpdateAndCacheAuthoritiesService.updateAuthoritiesOfAllTenant();
 *     // 基于 SCOPE
 *     UpdateAndCacheAuthoritiesService.updateAuthoritiesOfAllScopes();
 * </pre>
 * 3. 实现此 {@link RolePermissionsService} 接口, 不需要执行上两种方法的操作, 已通过 AOP 方式实现发布 UpdateRolesAuthoritiesEvent 事件.
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/6 23:33
 */
@SuppressWarnings("unused")
public interface RolePermissionsService<T> {

    /**
     * 更新角色(roleId)所拥有的资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     * @param roleId        角色 Id
     * @param resourceIds   资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateResourcesOfRole(Long roleId, Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新角色(roleId)所拥有的资源信息的接口逻辑");
    }
    /**
     * 更新多租户的角色(roleId)所拥有的资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     * @param tenantAuthority   多租户权限, 如: TENANT_110110, 110110 为租户 ID
     * @param roleId            角色 Id
     * @param resourceIds       资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateResourcesOfTenant(String tenantAuthority, Long roleId, Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新 多租户 的角色(roleId)所拥有的资源信息的接口逻辑");
    }

    /**
     * 更新 scope 的角色(roleId)所拥有的资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     * @param scopeAuthority    scope 权限, 如: SCOPE_read
     * @param roleId            角色 Id
     * @param resourceIds       资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateResourcesOfScope(String scopeAuthority, Long roleId, Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新 scope 的角色(roleId)所拥有的资源信息的接口逻辑");
    }

    /**
     * 根据 角色 获取所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param role  角色, 例如: ROLE_A
     * @return      返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    default List<T> findAllResourcesByRole(String role) throws RolePermissionsException {
        throw new RuntimeException("未实现根据 角色 获取所有的资源(Resources)信息的接口逻辑");
    }

    /**
     * 根据 租户权限 获取所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param tenantAuthority   多租户权限, 如: TENANT_110110, 110110 为租户 ID
     * @param role              角色, 例如: ROLE_A
     * @return                  返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    default List<T> findAllResourcesByTenant(String tenantAuthority, String role) throws RolePermissionsException {
        throw new RuntimeException("未实现根据 租户Id 获取所有的资源(Resources)信息的接口逻辑");
    }

    /**
     * 根据 scope 获取所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param scopeAuthority    scope 权限, 如: SCOPE_read
     * @param role              角色, 例如: ROLE_A
     * @return                  返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    default List<T> findAllResourcesByScope(String scopeAuthority, String role) throws RolePermissionsException {
        throw new RuntimeException("未实现根据 scope 获取所有的资源(Resources)信息的接口逻辑");
    }

}

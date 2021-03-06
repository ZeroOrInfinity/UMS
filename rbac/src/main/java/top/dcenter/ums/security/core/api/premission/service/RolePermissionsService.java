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

import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import top.dcenter.ums.security.core.exception.RolePermissionsException;
import top.dcenter.ums.security.core.premission.enums.PermissionType;

import java.util.List;

/**
 * 更新与查询基于(角色/多租户/SCOPE)的角色资源服务接口. 主要用于给角色添加权限的操作.<br>
 * 注意: <br>
 * 1. 在添加资源时, 通过{@link PermissionType#getPermission()} 来规范的权限格式, 因为要支持 restful 风格的 Api,
 * 在授权时需要对 {@link HttpMethod} 与对应的权限进行匹配判断<br>
 * 2. 如果实现了 {@link UpdateCacheOfRolesResourcesService} 接口, 未实现 {@link RolePermissionsService} 接口, 修改或添加基于"角色/多租户/SCOPE
 * "的资源权限时一定要调用 {@link UpdateCacheOfRolesResourcesService} 对应的方法, 有两种方式: 一种发布事件, 另一种是直接调用对应服务;<br>
 * <pre>
 *     // 1. 推荐用发布事件(异步执行)
 *     applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, UpdateRoleResourcesDto);
 *     applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, UpdateRoleResourcesDto);
 *     applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, UpdateRoleResourcesDto);
 *     // 2. 直接调用服务
 *     // 角色权限资源
 *     UpdateCacheOfRolesResourcesService.updateAuthoritiesByRoleId(roleId, resourceClass, resourceIds);
 *     // 多租户的角色权限资源
 *     UpdateCacheOfRolesResourcesService.updateAuthoritiesByRoleIdOfTenant(tenantId, roleId, resourceClass, resourceIds);
 *     // SCOPE 的角色权限资源
 *     UpdateCacheOfRolesResourcesService.updateAuthoritiesByScopeId(scopeId, roleId, resourceClass, resourceIds);
 *     // 多租户的 SCOPE 的角色权限资源
 *     UpdateCacheOfRolesResourcesService.updateAuthoritiesByScopeIdOfTenant(tenantId, scopeId, roleId, resourceClass, resourceIds);
 * </pre>
 * 3. 实现此 {@link RolePermissionsService} 接口, 不需要执行上两种方法的操作, 已通过 AOP 方式实现发布 UpdateRolesResourcesEvent 事件.
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/6 23:33
 */
@SuppressWarnings("unused")
public interface RolePermissionsService<T> {

    /**
     * 更新角色(roleId)所拥有的 resourceIds 资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     * @param roleId        角色 Id
     * @param resourceIds   资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateResourcesByRoleId(@NonNull Long roleId, Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新角色(roleId)所拥有的 resourceIds 资源信息的接口逻辑");
    }
    /**
     * 更新多租户的角色(roleId)所拥有的 resourceIds 资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     * @param tenantId          多租户 ID
     * @param roleId            角色 Id
     * @param resourceIds       资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateResourcesByRoleIdOfTenant(@NonNull Long tenantId, @NonNull Long roleId,
                                                    Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新 多租户 的角色(roleId)所拥有的 resourceIds 资源信息的接口逻辑");
    }

    /**
     * 更新 scopeId 的角色(roleId)所拥有的资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     * @param scopeId           scope id
     * @param roleId            角色 Id
     * @param resourceIds       资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateResourcesByScopeId(@NonNull Long scopeId, @NonNull Long roleId,
                                             Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新 scopeId 的角色(roleId)所拥有的资源信息的接口逻辑");
    }

    /**
     * 更新多租户的 scopeId 的角色(roleId)所拥有的资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     *
     * @param tenantId        多租户 ID
     * @param scopeId         scope id
     * @param roleId          角色 Id
     * @param resourceIds     资源 Ids
     * @return 是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateResourcesByScopeIdOfTenant(@NonNull Long tenantId, @NonNull Long scopeId,
                                                     @NonNull Long roleId,
                                                     Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新多租户的 scopeId 的角色(roleId)所拥有的资源信息的接口逻辑");
    }

    /**
     * 根据 roleId 获取所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param roleId  角色, 例如: ROLE_A
     * @return        返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    @NonNull
    default List<T> findAllResourcesByRoleId(@NonNull Long roleId) throws RolePermissionsException {
        throw new RuntimeException("未实现根据 roleId 获取所有的资源(Resources)信息的接口逻辑");
    }

    /**
     * 根据 租户Id 获取 指定 roleId 所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param tenantId          多租户 ID
     * @param roleId            角色 Id
     * @return                  返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    @NonNull
    default List<T> findAllResourcesByRoleIdOfTenant(@NonNull Long tenantId,
                                             @NonNull Long roleId) throws RolePermissionsException {
        throw new RuntimeException("未实现根据 租户Id 获取 指定 roleId 所有的资源(Resources)信息的接口逻辑");
    }

    /**
     * 根据 scopeId 获取指定 roleId 的所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param scopeId           scope id
     * @param roleId            角色 ID
     * @return                  返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    @NonNull
    default List<T> findAllResourcesByScopeId(@NonNull Long scopeId,
                                            @NonNull Long roleId) throws RolePermissionsException {
        throw new RuntimeException("未实现根据 scopeId 获取指定 roleId 的所有的资源(Resources)信息的接口逻辑");
    }

    /**
     * 根据多租户的 scopeId 获取所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param tenantId          多租户 ID
     * @param scopeId           scope id
     * @param roleId            角色 ID
     * @return                  返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    @NonNull
    default List<T> findAllResourcesByScopeIdOfTenant(@NonNull String tenantId, @NonNull Long scopeId,
                                                     @NonNull Long roleId) throws RolePermissionsException {
        throw new RuntimeException("未实现根据多租户的 scopeId 获取所有的资源(Resources)信息的接口逻辑");
    }

    /**
     * 获取更新资源的 Class.
     * @return  更新资源的 Class
     */
    @NonNull
    Class<T> getUpdateResourcesClass();

}

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
import top.dcenter.ums.security.core.exception.RolePermissionsException;

/**
 * 用于更新并缓存基于(角色/多租户/SCOPE)的角色权限服务接口, 每次更新角色的 uri(资源)权限时,需要调用此接口,
 * 推荐实现此 {@link RolePermissionsService} 接口, 会自动通过 AOP 方式实现发布 UpdateRolesResourcesEvent 事件,
 * 从而调用 {@link UpdateCacheOfRolesResourcesService} 对应的方法.<br>
 * 建议: <br>
 * 1. 基于 角色 的权限控制: 实现所有角色 uri(资源) 的权限 Map(roleAuthority, map(uri, Set(permission))) 的更新与缓存本机内存.
 * 2. 基于 SCOPE 的权限控制: 情况复杂一点, 但 SCOPE 类型比较少, 也还可以像 1 的方式实现缓存本机内存与更新.
 * 3. 基于 多租户 的权限控制: 情况比较复杂, 租户很少的情况下, 也还可以全部缓存在本机内存, 通常情况下全部缓存内存不现实, 只能借助于类似 redis 等的内存缓存.
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/8 21:15
 */
public interface UpdateCacheOfRolesResourcesService {

    /**
     * 更新角色(roleId)所拥有的 resourceIds 资源信息缓存.
     * @param roleId        角色 Id
     * @param resourceClass 更新的资源 class
     * @param resourceIds   资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    default boolean updateAuthoritiesByRoleId(@NonNull Long roleId, @NonNull Class<?> resourceClass,
                                              Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新角色(roleId)所拥有的 resourceIds 资源信息缓存的接口逻辑");
    }
    /**
     * 更新多租户的角色(roleId)所拥有的 resourceIds 资源信息缓存.
     * @param tenantId          多租户 ID
     * @param roleId            角色 Id
     * @param resourceClass     更新的资源 class
     * @param resourceIds       资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新缓存角色资源信息失败
     */
    default boolean updateAuthoritiesByRoleIdOfTenant(@NonNull Long tenantId, @NonNull Long roleId,
                                                      @NonNull Class<?> resourceClass,
                                                      Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新 多租户 的角色(roleId)所拥有的 resourceIds 资源信息缓存的接口逻辑");
    }

    /**
     * 更新 scopeId 的角色(roleId)所拥有的资源信息缓存.
     *
     * @param scopeId       scope id
     * @param roleId        角色 Id
     * @param resourceClass 更新的资源 class
     * @param resourceIds   资源 Ids
     * @return 是否操作成功
     * @throws RolePermissionsException 更新缓存角色资源信息失败
     */
    default boolean updateAuthoritiesByRoleIdOfScopeId(@NonNull Long scopeId, @NonNull Long roleId,
                                                       @NonNull Class<?> resourceClass,
                                                       Long... resourceIds) throws RolePermissionsException {
        throw new RuntimeException("未实现 更新 scopeId 的角色(roleId)所拥有的资源信息缓存的接口逻辑");
    }

    /**
     * 根据 groupId 更新 groupId 所拥有角色信息缓存
     * @param groupId    用户的 groupId
     * @param roleIds    用户的角色 ids
     * @return 是否操作成功
     * @throws RolePermissionsException 更新组的角色资源信息缓存失败
     */
    @NonNull
    default boolean updateRolesByGroupId(@NonNull Long groupId,
                                         Long... roleIds) throws RolePermissionsException {
        throw new RuntimeException("未实现根据 groupId 更新 groupId 所拥有角色信息缓存的接口逻辑");
    }

    /**
     * 基于多租户, 根据 groupId 更新 groupId 所拥有角色信息缓存
     *
     * @param tenantId  多租户 ID
     * @param groupId   用户的 groupId
     * @param roleIds   用户的角色 ids
     * @return 是否操作成功
     * @throws RolePermissionsException 更新组的角色资源信息缓存失败
     */
    @NonNull
    default boolean updateRolesByGroupIdOfTenant(@NonNull Long tenantId,
                                                 @NonNull Long groupId,
                                                 Long... roleIds) throws RolePermissionsException {
        throw new RuntimeException("未实现基于多租户, 根据 groupId 更新 groupId 所拥有角色信息缓存的接口逻辑");
    }

    /**
     * 初始化所有权限信息并缓存
     * 注意: 要考虑并发更新问题.
     * @throws RolePermissionsException 初始化所有权限信息并缓存失败
     */
    default void initAllAuthorities() throws RolePermissionsException {
        throw new RuntimeException("未实现初始化所有权限信息并缓存的接口逻辑");
    }

}
